/**
 * Created by Weiwei on 10/17/2014.
 */

var width = 1100;
var height = 700;

var colors = d3.scale.category20();

//init: force layout and svg canvas
var force = d3.layout.force().size([width, height]).linkDistance([70]).charge([-100]);
var svg = d3.select("#svgwrapper").append("svg").attr("width", width).attr("height", height).attr("id", "force");

//create the svg
function createSvg(datafile) {
    d3.json(datafile, function (dataset) {
        force.nodes(dataset.nodes).links(dataset.links).start();
        var edges = svg.selectAll("line").data(dataset.links).enter()
            .append("line").style("stroke", "#ccc").style("stroke-width", 1);
        var nodes = svg.selectAll("circle").data(dataset.nodes).enter()
            .append("circle").attr("r", 5).style("fill", function (d) {
                return colors(d.group)
            }).call(force.drag);
        nodes.append("title").text(function (d) {
            return d.name;
        });

        forceTick(force, edges, nodes);

        nodes.on("click", function (d) {

        });
    });
}

function forceTick(force, edges, nodes) {
    force.on("tick", function () {
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
}

//page-load-init
//var inboundOverview = "../data/test/inbound.test.json";
var inboundOverview = "../data/outsite2.json";
createSvg(inboundOverview);

//handle the click of button 'overview'
function loadInboundOverview() {
    d3.selectAll("line").remove();
    d3.selectAll("circle").remove();
    createSvg(inboundOverview);
}


var inMin = 0;
var inMax = 15;
var outMin = 0;
var outMax = 15;

//input-domain; output-range
var colorScale = d3.scale.linear().domain([inMin, inMax]).range([1, 255]);
var radiusScale = d3.scale.linear().domain([outMin, outMax]).range([5, 15]);

function jumpInHandler() {

    var nodes = svg.selectAll("circle");
    nodes.attr("r", function (d) {
        return Math.floor(radiusScale(d.jumpin));
    });

    nodes.style("fill", function (d) {
        var red = Math.floor(colorScale(d.jumpin));
        return "rgb(" + red + ",0,0)";
    });
}


function jumpOutHandler(){
    var nodes = svg.selectAll("circle");
    nodes.attr("r", function (d) {
        return Math.floor(radiusScale(d.jumpout));
    });

    nodes.style("fill", function (d) {
        var red = Math.floor(colorScale(d.jumpout));
        return "rgb(" + red + ",0,0)";
    });
}

function degreeHandler(){
    var nodes = svg.selectAll("circle");
    nodes.attr("r", function (d) {
        return Math.floor(Math.sqrt(d.weight));
    })
}