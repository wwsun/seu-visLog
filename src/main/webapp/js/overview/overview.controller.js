angular.module('vislog.overview', ['chart.js'])

    .constant('baseUrl', 'http://localhost:8080/vislog_restful/api/')
    .controller('OverviewCtrl', function ($http, baseUrl) {

        var vm = this;

        vm.date = "2015-07-01";

        vm.keyIndex = null;

        vm.distribution = {
            labels: [],
            data: [],
            series: []
        };

        vm.categoryDistribution = {
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

        // 核心参数指标
        vm.getKeyIndexByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/index/' + date).success(function (data) {
                vm.keyIndex = {
                    total: data.total,
                    bounce_rate: data.bounce_rate.toFixed(2),
                    inquiry_rate: data.inquiry_rate.toFixed(2)
                };
            });
        };

        // 按日期获取会话在不同时段的分布
        vm.getSessionDistributionByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/trend/' + date).success(function (data) {

                // 只保留奇数项
                var labels = data.hour.filter(function (item, index, array) {
                    return (index % 2 == 1)
                });

                vm.distribution.labels = labels.map(function (item, index, array) {
                    return item + "h";
                });

                // 只保留奇数项
                vm.distribution.data.push(data.dup.filter(function (item, index, array) {
                    return (index % 2 == 1)
                }));
                vm.distribution.series.push(vm.date);

            });
        };

        // 按日期获取会话在不同类别的分布
        vm.getCategoryDistributionByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/category/' + date).success(function (data) {
                var dups = [];
                var i;
                var n;

                for (i = 0, n = data.length; i < n; i++) {
                    vm.categoryDistribution.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.categoryDistribution.data.push(dups);
            })
        };

        // 响应按钮事件：在图形中增加新的对比数据
        vm.handleNewAnalysisRangeBtn = function (newDate) {
            if (angular.isDefined(newDate)) {
                vm.getKeyIndexByDate(newDate);
                vm.getSessionDistributionByDate(newDate);
                vm.getCategoryDistributionByDate(newDate);
            }
        };

        vm.getSearchEngineContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/se').success(function (data) {
                vm.searchEngines = data;
            });
        };

        vm.getCountriesContribution = function () {
            $http.get(baseUrl + 'sessions/overview/sources/countries').success(function (data) {
                vm.countryContribution = data;
            });
        };

        vm.getHotPages = function () {
            $http.get(baseUrl + 'sessions/overview/frequent/pages').success(function (data) {
                vm.hotPages = data;
            });
        };

        vm.getMainLandingCategories = function () {
            $http.get(baseUrl + 'sessions/overview/landings/categories').success(function (data) {
                var dups = [];
                var i, n;
                for (i = 0, n = data.length; i < n; i++) {
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
                for (i = 0, n = data.length; i < n; i++) {
                    vm.mainDropOffCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.mainDropOffCategories.data.push(dups);
            });
        };

        vm.getKeyIndexByDate(vm.date);
        vm.getSessionDistributionByDate(vm.date);
        vm.getCategoryDistributionByDate(vm.date);
        vm.getSearchEngineContribution();
        vm.getCountriesContribution();
        vm.getHotPages();
        //vm.getHotCategories();
        vm.getMainLandingCategories();
        vm.getMainDropOffCategories();

    });