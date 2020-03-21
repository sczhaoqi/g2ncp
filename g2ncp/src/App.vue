<template>
  <div id="app">
    <span>{{ title }}</span>
    <div id="c1"></div>
    <div id="c2"></div>
  </div>
</template>

<script>
import { Chart, registerAnimation } from "@antv/g2";

function handleData(source) {
  source.sort((a, b) => {
    return a.value - b.value;
  });

  return source;
}
function getUrlKey(name) {
  return (
    decodeURIComponent(
      (new RegExp("[?|&]" + name + "=" + "([^&;]+?)(&|#|;|$)").exec(
        location.href
        /* eslint-disable */
      ) || [, ""])[1].replace(/\+/g, "%20")
    ) || null
  );
}

export default {
  name: "App",
  components: {},
  data() {
    return {
      title: "ncp数据",
      c1data: {},
      c1chart: null,
      isSeaArea: true
    };
  },
  mounted() {
    this.isSeaArea = getUrlKey("seaArea") != null;
    this.loadData();
    this.initCharts();
  },
  methods: {
    initCharts() {
      this.chart = new Chart({
        container: "c1", // 指定图表容器 ID
        width: 600, // 指定图表宽度
        height: 300 // 指定图表高度
      });
      // Step 2: 载入数据源
      this.chart.data(this.c1data);

      // Step 3: 创建图形语法，绘制柱状图
      this.chart.interval().position("genre*sold");

      // Step 4: 渲染图表
      this.chart.render();

      registerAnimation("label-appear", (element, animateCfg, cfg) => {
        const label = element.getChildren()[0];
        const coordinate = cfg.coordinate;
        const startX = coordinate.start.x;
        const finalX = label.attr("x");
        const labelContent = label.attr("text");

        label.attr("x", startX);
        label.attr("text", 0);

        const distance = finalX - startX;
        label.animate(ratio => {
          const position = startX + distance * ratio;
          const text = (labelContent * ratio).toFixed(0);

          return {
            x: position,
            text
          };
        }, animateCfg);
      });

      registerAnimation("label-update", (element, animateCfg, cfg) => {
        const startX = element.attr("x");
        const startY = element.attr("y");
        // @ts-ignore
        const finalX = cfg.toAttrs.x;
        // @ts-ignore
        const finalY = cfg.toAttrs.y;
        const labelContent = element.attr("text");
        // @ts-ignore
        const finalContent = cfg.toAttrs.text;

        const distanceX = finalX - startX;
        const distanceY = finalY - startY;
        const numberDiff = +finalContent - +labelContent;

        element.animate(ratio => {
          const positionX = startX + distanceX * ratio;
          const positionY = startY + distanceY * ratio;
          const text = (+labelContent + numberDiff * ratio).toFixed(0);

          return {
            x: positionX,
            y: positionY,
            text
          };
        }, animateCfg);
      });
      // chart 2
      fetch("/api/ncpdata/" + this.isSeaArea)
        .then(res => res.json())
        .then(data => {
          let count = 0;
          let chart;
          let interval;

          function countUp() {
            if (count === 0) {
              chart = new Chart({
                container: "c2",
                autoFit: true,
                height: 500,
                padding: [20, 60]
              });
              // @ts-ignore
              chart.data(handleData(Object.values(data)[count]));
              chart.coordinate("rect").transpose();
              chart.legend(false);
              chart.tooltip(false);
              // chart.axis('value', false);
              chart.axis("city", {
                animateOption: {
                  update: {
                    duration: 1000,
                    easing: "easeLinear"
                  }
                }
              });
              chart.annotation().text({
                position: ["95%", "90%"],
                content: Object.keys(data)[count],
                style: {
                  fontSize: 40,
                  fontWeight: "bold",
                  fill: "#ddd",
                  textAlign: "end"
                }
              });
              chart
                .interval()
                .position("city*value")
                .color("city")
                .label("value", () => {
                  // if (value !== 0) {
                  return {
                    animate: {
                      appear: {
                        animation: "label-appear",
                        delay: 0,
                        duration: 1000,
                        easing: "easeLinear"
                      },
                      update: {
                        animation: "label-update",
                        duration: 1000,
                        easing: "easeLinear"
                      }
                    },
                    offset: 5
                  };
                  // }
                })
                .animate({
                  appear: {
                    duration: 1000,
                    easing: "easeLinear"
                  },
                  update: {
                    duration: 1000,
                    easing: "easeLinear"
                  }
                });

              chart.render();
            } else {
              chart.annotation().clear(true);
              chart.annotation().text({
                position: ["95%", "90%"],
                content: Object.keys(data)[count],
                style: {
                  fontSize: 40,
                  fontWeight: "bold",
                  fill: "#ddd",
                  textAlign: "end"
                }
              });
              // @ts-ignore
              chart.changeData(handleData(Object.values(data)[count]));
            }

            ++count;

            if (count === Object.keys(data).length) {
              clearInterval(interval);
            }
          }

          countUp();
          interval = setInterval(countUp, 1200);
        });
    },
    loadData() {
      this.c1data = [
        { genre: "Sports", sold: 275 },
        { genre: "Strategy", sold: 115 },
        { genre: "Action", sold: 120 },
        { genre: "Shooter", sold: 350 },
        { genre: "Other", sold: 150 }
      ];
    }
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
