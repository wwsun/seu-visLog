angular.module('vislog.path', ['chart.js'])

    .constant('baseUrl', 'http://localhost:8080/vislog-restful/api/')

    .controller('PathCtrl', function ($http, baseUrl) {

        var vm = this;

        vm.dateRange = {
            start: '2014-10-22',
            end: '2014-10-23'
        };
    });