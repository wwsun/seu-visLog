angular.module('vislog.overview', ['chart.js'])

    .constant('baseUrl', 'http://localhost:8080/vislog-restful/api/')
    .controller('OverviewCtrl', function ($http, baseUrl) {

        var vm = this;

        vm.status = null;
        vm.distribution = {
            labels: [],
            data: [],
            series:[]
        };

        vm.searchEngines = null;

        vm.getOverviewNumbers = function() {
            $http.get(baseUrl + 'sessions/overview/status').success(function (data) {
                vm.status = data;
            });
        };

        vm.getSessionDistributionByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/' + date).success(function (data) {
                vm.distribution.labels = data.hour;
                vm.distribution.data.push(data.dup);
                vm.distribution.series.push('sessions');
            });
        };

        vm.getSearchEngineContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/se').success(function (data) {
                vm.searchEngines = data;
            });
        };

        vm.getOverviewNumbers();
        vm.getSessionDistributionByDate("2014-10-22");
        vm.getSearchEngineContribution();

    });