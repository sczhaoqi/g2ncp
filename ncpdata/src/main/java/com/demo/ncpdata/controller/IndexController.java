package com.demo.ncpdata.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("")
@Slf4j
public class IndexController {

    @GetMapping("")
    public String hello() {
        return "Hello,Wolrd!";
    }

    @GetMapping("data")
    public String testData() {
        return "{}";
    }

    @GetMapping("gdpdata")
    public String gdpData() {
        String data = "{}";
        try {
            data = new String(Files.readAllBytes(ResourceUtils.getFile("classpath:china-gdp.json").toPath()), "UTF-8");
        } catch (final IOException e) {
            log.error("Fail to Open file");
        }
        return data;
    }

    @GetMapping("ncpdata/{seaArea}")
    public String ncpData(@PathVariable Boolean seaArea) throws MalformedURLException, IOException {
        File file = ResourceUtils.getFile("classpath:DXYArea.csv");
        // FileUtils.copyURLToFile(
        // new
        // URL("https://raw.githubusercontent.com/BlankerL/DXY-COVID-19-Data/master/csv/DXYArea.csv"),
        // file);
        final List<String> lineDatas = Files.readAllLines(file.toPath());
        final String[] headers = lineDatas.get(0).split(",");
        // continentName,continentEnglishName,countryName,countryEnglishName,provinceName,provinceEnglishName,
        // province_zipCode,province_confirmedCount,province_suspectedCount,province_curedCount,province_deadCount,
        // cityName,cityEnglishName,city_zipCode,city_confirmedCount,city_suspectedCount,city_curedCount,city_deadCount,
        // updateTime
        // We need dayno:{ city:province,value:province_confirmedCount}
        final int dateIndex = Arrays.binarySearch(headers, "updateTime");
        final int countryIndex = Arrays.binarySearch(headers, "countryName");
        final int provinceIndex = Arrays.binarySearch(headers, "provinceName");
        final int provinceConfirmedCountIndex = Math.abs(Arrays.binarySearch(headers, "province_confirmedCount"));
        log.info(String.format(" %d-%d-%d-%d", dateIndex, countryIndex, provinceIndex, provinceConfirmedCountIndex));
        final Map<String, Map<String, Integer>> provincConfirmedNums = new HashMap<>();
        lineDatas.subList(1, lineDatas.size() - 1).stream().map(line -> line.split(","))
                .filter(line -> seaArea ^ ("中国".equals(line[countryIndex]))).forEach(line -> {
                    final String dayno = line[line.length - 1 - headers.length + 1 + dateIndex].substring(0, 10)
                            .replace("-", "");
                    final String province = line[provinceIndex];
                    final String confirmed = line[provinceConfirmedCountIndex];
                    final Map<String, Integer> pMap = provincConfirmedNums.getOrDefault(dayno,
                            new HashMap<String, Integer>());
                    if (pMap.getOrDefault(province, -1) > Integer.valueOf(confirmed)) {
                        // skip
                    } else {
                        pMap.put(province, Integer.valueOf(confirmed));
                        provincConfirmedNums.put(dayno, pMap);
                    }
                });

        Map<String, Object> datass = provincConfirmedNums.entrySet().stream().collect(Collectors.toMap(
                (e) -> e.getKey(),
                (e) -> e.getValue().entrySet().stream()
                        .map(en -> String.format("{\"value\": %d,\"city\": \"%s\"}", en.getValue(), en.getKey()))
                        .collect(Collectors.toList())));
        return JSON.toJSONString(datass).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
    }
}