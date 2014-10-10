/**
 * Created by Weiwei on 10/8/2014.
 */
var nodes = [
    {id: '10.4.42.1', type: 'router', status: 1},
    {id: '10.4.43.1', type: 'switch', status: 1, expand: true},
    {id: '10.4.44.1', type: 'switch', status: 1},
    {id: '10.4.45.1', type: 'switch', status: 0}

];

var childNodes = [
    {id: '10.4.43.2', type: 'switch', status: 1},
    {id: '10.4.43.3', type: 'switch', status: 1}

];

var links = [
    {source: '10.4.42.1', target: '10.4.43.1'},
    {source: '10.4.42.1', target: '10.4.44.1'},
    {source: '10.4.42.1', target: '10.4.45.1'}
];

var childLinks = [
    {source: '10.4.43.1', target: '10.4.43.2'},
    {source: '10.4.43.1', target: '10.4.43.3'},
    {source: '10.4.43.2', target: '10.4.43.3'}
];


function Topology(ele) {
    var div = document.getElementById(ele);
    var w = 500;
    var h = 500;

    this.force = d3.layout.force().gravity(.05).distance(200).charge(-200).size([w, h]);

    this.nodes = this.force.nodes();
    this.links = this.force.links();

    this.clickFn = function () {

    };

    this.svg = d3.select(ele).append("svg")
        .attr("width", w)
        .attr("height", h)
        .attr("pointer-events", "all");

    this.force.on("tick", function() {
        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; });
    });

}

//add node
Topology.prototype.addNode = function (node) {
    this.nodes.push(node);
};

Topology.prototype.addNodes = function (nodes) {

};

Topology.prototype.findNode = function (id) {
    var nodes = this.nodes;
    for(var i in nodes){
        if(nodes[i]['id']==id)
            return nodes[i];
    }
};

var topology = new Topology("container");