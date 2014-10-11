/**
 * Created by Weiwei on 10/8/2014.
 */

var width = 1100;
var height = 500;

var colors = d3.scale.category20();

//1.init
var force = d3.layout.force()
    .size([width,height])
    .linkDistance([100])
    .charge([-100]);



//3. load data
function updateSvg(sourceFile) {

    d3.json(sourceFile, function (dataset) {

        force.nodes(dataset.nodes)
            .links(dataset.links)
            .start();

        //2.create svg
        var svg = d3.select("#svgwrapper")
            .append("svg")
            .attr("width", width)
            .attr("height", height)
            .attr("id","forcesvg");

        //4.create links
        var edges = svg.selectAll("line")
            .data(dataset.links)
            .enter()
            .append("line")
            .style("stroke", "#ccc")
            .style("stroke-width", 1);

        //5.create nodes
        var nodes = svg.selectAll("circle")
            .data(dataset.nodes)
            .enter()
            .append("circle")
            .attr("r", 10)
            .style("fill", "#009966")
            .call(force.drag);


        nodes.append("title").text(function (d) {
            return d.name;
        });


        //6.tick
        force.on("tick", function(){
            edges.attr("x1", function (d) {
                return d.source.x;
            })
                .attr("y1", function (d) {
                    return d.source.y;
                })
                .attr("x2", function (d) {
                    return d.target.x;
                })
                .attr("y2", function (d) {
                    return d.target.y;
                });
            nodes.attr("cx", function (d) {
                return d.x;
            })
                .attr("cy", function (d) {
                    return d.y;
                });
        });

        nodes.on("click", function (d) {
            //if(d3.event.isDefaultPrevented()) return; //click suppressed
            console.log("You clicked " + d.name);
        });

    });
}

var overviewData = "data/overview.json",
    outsiteData = "data/out.json";

updateSvg(overviewData);

function loadOverview() {
    removeSvg();
    updateSvg(overviewData);
}

function loadOutsite() {
    removeSvg();
    updateSvg(outsiteData);
}

function removeSvg() {
    var svg = document.getElementById("forcesvg");
    svg.parentNode.removeChild(svg);
}