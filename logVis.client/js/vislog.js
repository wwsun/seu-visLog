/**
 * Created by Weiwei on 10/8/2014.
 */

var width = 1100;
var height = 700;

var colors = d3.scale.category20();

//1.init
var force = d3.layout.force()
    .size([width,height])
    .linkDistance([100])
    .charge([-100]);

var pie = d3.layout.pie();




function drawPie(dataset){

    var outerRadius = 150;
    var innerRadius = 0;

    if(d3.select("#pie")) {
        removeSvg("pie");
    }

    var svg = d3.select("#piechart").append("svg").attr("width", 300).attr("height",300).attr("id","pie");

    var arc = d3.svg.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius);


    var arcs = svg.selectAll("g.arc")
        .data(pie(dataset))
        //.transition()
        .enter()
        .append("g")
        .attr("class", "arc")
        .attr("transform", "translate(" + outerRadius + "," + outerRadius + ")");

    //Draw arc paths
    arcs.append("path")
        .attr("fill", function(d, i) {
            return colors(i);
        })
        .attr("d", arc);

    //Labels
    arcs.append("text")
        .attr("transform", function(d) {
            return "translate(" + arc.centroid(d) + ")";
        })
        .attr("text-anchor", "middle")
        .text(function(d) {
            return d.value;
        });
}

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
            .style("stroke-width", function(d){
                return d.value;
            });

        //5.create nodes
        var nodes = svg.selectAll("circle")
            .data(dataset.nodes)
            .enter()
            .append("circle")
            .attr("r", function(d){
                return 7;
            })
            .style("fill", function(d){
                return colors(d.group);
            })
            .call(force.drag);


        nodes.append("title").text(function (d) {
            return d.name;
        });

        edges.append("title").text(function (d) {
            return d.value;
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
            //console.log("You clicked " + d.name);
            d3.select("#jumpin").text(d.name);
            d3.select("#jumpout").text(d.group);

            //virtual dataset

            var dataset = [];

            for(var i=0; i< 5; i++){
                var newData = Math.floor(Math.random()*10);
                dataset.push(newData);
            }
            drawPie(dataset);

            d3.select("#clickinfo").text("You clicked " + d.name);
        });

    });
}

var overviewData = "data/overview.json",
    outsiteData = "data/outsite2.json";

updateSvg(overviewData);

function loadOverview() {
    removeSvg("forcesvg");
    updateSvg(overviewData);
}

function loadOutsite() {
    removeSvg("forcesvg");
    updateSvg(outsiteData);
    loadOutsiteDataTable();//load data table
}

function removeSvg(elementId) {
    var svg = document.getElementById(elementId);
    if(svg){
        svg.parentNode.removeChild(svg);
    }
}

function loadOutsiteDataTable() {

    $('#example').dataTable( {
        "ajax": "data/nodes.txt",
        "columns": [
            { "data": "name" },
            { "data": "size" },
            { "data": "group" }
        ]
    } );
}