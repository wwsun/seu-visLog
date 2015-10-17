angular.module('vislog.path', ['chart.js'])

    .controller('PathCtrl', function ($http, sessionPathData) {

        var vm = this;

        vm.progressData = 10; // test

        vm.dateRange = {
            startDate: '2015-07-01',
            endDate: '2015-07-02',
            edgeValue: 4.5
        };

        //vm.pathData = sessionPathData.pathData;

        vm.orderedAttribute = null;

        vm.clickedNode = {
            labels: ["Through", "Drop-off"],
            in_degree: 1,
            out_degree: 1
        };

        vm.orderBy = function (item) {
            vm.orderedAttribute = item;
        }
    });