angular.module('vislog.path')

    .directive('pathChart', pathChart);

function pathChart() {
    function link(scope, el, attr) {

        console.log(scope);
        var energy = scope.data;

        var margin = {top: 1, right: 1, bottom: 6, left: 1},
            width = 800,
            height = 700;

        var formatNumber = d3.format(",.0f"),
            format = function (d) {
                return "weight " + formatNumber(d);
            },
            color = d3.scale.category20();

        var svg = d3.select(el[0]).append("svg")
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var sankey = d3.sankey()
            .nodeWidth(15)
            .nodePadding(10)
            .size([width, height]);

        var path = sankey.link();

        sankey
            .nodes(energy.nodes)
            .links(energy.links)
            .layout(16);

        var links = svg.append("g").selectAll(".link")
            .data(energy.links)
            .enter().append("path")
            .attr("class", "link")
            .attr("d", path)
            .style("stroke-width", function (d) {
                return Math.max(1, d.dy);
            })
            .sort(function (a, b) {
                return b.dy - a.dy;
            });

        links.append("title")
            .text(function (d) {
                return d.source.url + " ¡ú " + d.target.url + "\n" + format(d.value);
            });

        var node = svg.append("g").selectAll(".node")
            .data(energy.nodes)
            .enter().append("g")
            .attr("class", "node")
            .attr("transform", function (d) {
                return "translate(" + d.x + "," + d.y + ")";
            })
            .call(d3.behavior.drag()
                .origin(function (d) {
                    return d;
                })
                .on("dragstart", function () {
                    this.parentNode.appendChild(this);
                })
                .on("drag", dragmove));

        node.append("rect")
            .attr("height", function (d) {
                return d.dy;
            })
            .attr("width", sankey.nodeWidth())
            .style("fill", function (d) {
                return (d.color = color(d.url.replace(/ .*/, "")));
            })
            .style("stroke", function (d) {
                return d3.rgb(d.color).darker(2);
            })
            .append("title")
            .text(function (d) {
                return d.url + "\n" + format(d.value);
            });

        node.append("text")
            .attr("x", -6)
            .attr("y", function (d) {
                return d.dy / 2;
            })
            .attr("dy", ".35em")
            .attr("text-anchor", "end")
            .attr("transform", null)
            .text(function (d) {
                return d.title;
            })
            .filter(function (d) {
                return d.x < width / 2;
            })
            .attr("x", 6 + sankey.nodeWidth())
            .attr("text-anchor", "start");

        node.on('click', nodeClickHandler);

        function nodeClickHandler(d) {
            // d is the data item for the current data/element pair
            scope.$apply(function () {
                scope.clickedPoint = d;
                //console.log(d);
            });
        }

        function dragmove(d) {
            d3.select(this).attr("transform", "translate(" + d.x + "," + (d.y = Math.max(0, Math.min(height - d.dy, d3.event.y))) + ")");
            sankey.relayout();
            links.attr("d", path);
        }
    }

    return {
        link: link,
        restrict: 'E',
        scope: {data: '=', clickedPoint: '='}
    };
}