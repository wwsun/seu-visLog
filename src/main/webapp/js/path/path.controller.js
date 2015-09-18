angular.module('vislog.path', ['chart.js'])

    //.constant('baseUrl', 'http://localhost:8080/vislog-restful/api/')

    .controller('PathCtrl', function ($http, sessionPathData) {

        var vm = this;

        vm.dateRange = {
            start: '2014-10-22',
            end: '2014-10-23'
        };

        vm.pathData = sessionPathData.pathData;

        vm.orderedAttribute = null;

        vm.clickedNode = {
            labels: ["Through", "Drop-off"],
            in_degree: 1,
            out_degree: 1
        };

        vm.orderBy = function(item) {
            vm.orderedAttribute = item;
        }


    });