angular.module('vislog.overview', ['chart.js'])

    .constant('baseUrl', 'http://localhost:8080/vislog-restful/api/')
    .controller('OverviewCtrl', function ($http, baseUrl) {

        var vm = this;

        vm.status = null;

        vm.date = "2014-10-22";

        vm.distribution = {
            labels: [],
            data: [],
            series:[]
        };

        vm.hotCategories = {
            labels: [],
            data: []
        };

        vm.searchEngines = null;
        vm.countryContribution = [];
        vm.hotPages = null;
        vm.mainLandingCategories = {
            labels: [],
            data: []
        };

        vm.mainDropOffCategories = {
            labels: [],
            data: []
        };

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

        vm.handleNewAnalysisRangeBtn = function (newDate) {
            if (angular.isDefined(newDate)) {
                vm.getSessionDistributionByDate(newDate);
            }
        };

        vm.getSearchEngineContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/se').success(function (data) {
                vm.searchEngines = data;
            });
        };

        vm.getCountriesContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/countries').success(function (data){
                vm.countryContribution = data;
            });
        };

        vm.getHotPages = function() {
            $http.get(baseUrl + 'sessions/overview/frequent/pages').success(function (data) {
                vm.hotPages = data;
            });
        };

        vm.getHotCategories = function() {
            $http.get(baseUrl + 'sessions/overview/frequent/categories').success(function (data) {
                var dups = [];
                var i,n;

                for (i=0, n=data.length; i <n; i++) {
                    vm.hotCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.hotCategories.data.push(dups);
            });
        };

        vm.getMainLandingCategories = function() {
            $http.get(baseUrl + 'sessions/overview/landings/categories').success(function (data) {
                var dups = [];
                var i, n;
                for (i=0, n=data.length; i<n; i++) {
                    vm.mainLandingCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.mainLandingCategories.data.push(dups);
            });
        };

        vm.getMainDropOffCategories = function () {
            $http.get(baseUrl + 'sessions/overview/dropoff/categories').success(function (data) {
                var dups = [];
                var i, n;
                for (i=0, n=data.length; i<n; i++) {
                    vm.mainDropOffCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.mainDropOffCategories.data.push(dups);
            });
        };

        vm.getOverviewNumbers();
        vm.getSessionDistributionByDate("2014-10-22");
        vm.getSearchEngineContribution();
        vm.getCountriesContribution();
        vm.getHotPages();
        vm.getHotCategories();
        vm.getMainLandingCategories();
        vm.getMainDropOffCategories();

    });