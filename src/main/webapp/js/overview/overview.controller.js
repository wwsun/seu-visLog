angular.module('vislog.overview', ['chart.js'])

    .constant('baseUrl', 'http://localhost:8080/vislog-restful/api/')
    .controller('OverviewCtrl', function ($http, baseUrl) {

        var vm = this;

        vm.status = null;

        vm.startDate = "2014-08-08";
        vm.endDate = "2014-08-12";

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

        vm.getOverviewNumbersByRange = function(startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/status/' + startDate + '/' + endDate).success(function (data) {
                vm.status = data;
            });
        };

        vm.getSessionDistributionByRange = function (startDate,endDate) {
            $http.get(baseUrl + 'sessions/distribution/' + startDate + '/' + endDate).success(function (data) {
                vm.distribution.data = [];
                vm.distribution.series = [];
                vm.distribution.labels = data.hour;
                vm.distribution.data.push(data.dup);
                vm.distribution.series.push('sessions');
            });
        };

        vm.handleNewAnalysisRangeBtn = function (startDate,endDate) {
            if (angular.isDefined(startDate) && angular.isDefined(endDate)) {
                vm.getSessionDistributionByRange(startDate,endDate);
                vm.getOverviewNumbersByRange(startDate,endDate);
                vm.getSearchEngineContributionByRange(startDate,endDate);
                vm.getCountriesContributionByRange(startDate,endDate);
                vm.getHotPagesByRange(startDate,endDate);
                vm.getHotCategoriesByRange(startDate,endDate);
                vm.getMainLandingCategoriesByRange(startDate,endDate);
                vm.getMainDropOffCategoriesByRange(startDate,endDate);
            }
        };

        vm.getSearchEngineContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/se').success(function (data) {
                vm.searchEngines = data;
            });
        };

        vm.getSearchEngineContributionByRange = function (startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/sources/se/' + startDate + '/' + endDate + '/10').success(function (data) {
                vm.searchEngines = data;
            });
        };

        vm.getCountriesContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/countries').success(function (data){
                vm.countryContribution = data;
            });
        };

        vm.getCountriesContributionByRange = function (startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/sources/countries/' + startDate + '/' + endDate + '/10').success(function (data){
                vm.countryContribution = data;
            });
        };

        vm.getHotPages = function() {
            $http.get(baseUrl + 'sessions/overview/frequent/pages').success(function (data) {
                vm.hotPages = data;
            });
        };

        vm.getHotPagesByRange = function(startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/frequent/pages/'  + startDate + '/' + endDate + '/20').success(function (data) {
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

        vm.getHotCategoriesByRange = function(startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/frequent/categories/'  + startDate + '/' + endDate + '/7').success(function (data) {
                var dups = [];
                var i,n;
                vm.hotCategories.labels = [];
                vm.hotCategories.data = [];

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

        vm.getMainLandingCategoriesByRange = function(startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/landings/categories/'  + startDate + '/' + endDate + '/10').success(function (data) {
                var dups = [];
                var i, n;
                vm.mainLandingCategories.labels = [];
                vm.mainLandingCategories.data = [];

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

        vm.getMainDropOffCategoriesByRange = function (startDate,endDate) {
            $http.get(baseUrl + 'sessions/overview/dropoff/categories/'  + startDate + '/' + endDate + '/10').success(function (data) {
                var dups = [];
                var i, n;
                vm.mainDropOffCategories.labels = [];
                vm.mainDropOffCategories.data = [];

                for (i=0, n=data.length; i<n; i++) {
                    vm.mainDropOffCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.mainDropOffCategories.data.push(dups);
            });
        };

        vm.getOverviewNumbersByRange("2014-08-08","2014-08-11");
        vm.getSessionDistributionByRange("2014-08-08","2014-08-11");
        vm.getSearchEngineContributionByRange("2014-08-08","2014-08-11");
        vm.getCountriesContributionByRange("2014-08-08","2014-08-11");
        vm.getHotPagesByRange("2014-08-08","2014-08-11");
        vm.getHotCategoriesByRange("2014-08-08","2014-08-11");
        vm.getMainLandingCategoriesByRange("2014-08-08","2014-08-11");
        vm.getMainDropOffCategoriesByRange("2014-08-08","2014-08-11");

    });
