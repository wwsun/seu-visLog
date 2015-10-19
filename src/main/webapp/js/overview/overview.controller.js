angular.module('vislog.overview', ['chart.js'])

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
            data: [],
            series: []
        };

        vm.searchEngines = {
            labels: [],
            data: []
        };
        vm.countryContribution = {
            labels: [],
            data: []
        };
        vm.hotPages = null;
        vm.mainLandingCategories = {
            labels: [],
            data: [],
            series: []
        };

        vm.mainDropOffCategories = {
            labels: [],
            data: [],
            series: []
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
                vm.categoryDistribution.series.push(date);
            })
        };

        // 按日期获取搜索引擎贡献
        vm.getSearchEngineContributionByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/sources/se/' + date).success(function (data) {

                // data init
                vm.searchEngines.labels.length = 0;
                vm.searchEngines.data.length = 0;

                var i;
                var n;

                for (i = 0, n = data.length; i < n; i++) {
                    vm.searchEngines.labels.push(data[i].name);
                    vm.searchEngines.data.push(data[i].dup);
                }

            });
        };

        // 按日期获取主要流量的国家分布
        vm.getCountriesContributionByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/sources/countries/' + date).success(function (data) {

                // data init
                vm.countryContribution.data.length = 0;
                vm.countryContribution.labels.length = 0;

                var i;
                var n;

                for (i = 0, n = data.length; i < n; i++) {
                    vm.countryContribution.labels.push(data[i].name);
                    vm.countryContribution.data.push(data[i].dup);
                }
            });
        };

        // 按日期获取当日流量在类别上的分布情况
        vm.getMainLandingCategoriesByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/landings/categories/' + date).success(function (data) {
                var dups = [];
                var i, n;
                for (i = 0, n = data.length; i < n; i++) {
                    vm.mainLandingCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.mainLandingCategories.data.push(dups);
                vm.mainLandingCategories.series.push(date);
            });
        };

        // 按日期获取当日流量在跳出时在类别上的分布情况
        vm.getMainDropOffCategoriesByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/dropoff/categories/' + date).success(function (data) {
                var dups = [];
                var i, n;
                for (i = 0, n = data.length; i < n; i++) {
                    vm.mainDropOffCategories.labels.push(data[i].name);
                    dups.push(data[i].dup);
                }
                vm.mainDropOffCategories.data.push(dups);
                vm.mainDropOffCategories.series.push(date);
            });
        };

        // 按日期获取当日用户的频繁访问页面
        vm.getHotPagesByDate = function (date) {
            $http.get(baseUrl + 'sessions/distribution/frequent/pages/' + date).success(function (data) {
                vm.hotPages = data;
            });
        };

        // 响应按钮事件：在图形中增加新的对比数据
        vm.handleNewAnalysisRangeBtn = function (newDate) {
            if (angular.isDefined(newDate)) {
                vm.getKeyIndexByDate(newDate);
                vm.getSessionDistributionByDate(newDate);
                vm.getCategoryDistributionByDate(newDate);
                vm.getSearchEngineContributionByDate(newDate);
                vm.getCountriesContributionByDate(newDate);
                vm.getMainLandingCategoriesByDate(newDate);
                vm.getMainDropOffCategoriesByDate(newDate);
                vm.getHotPagesByDate(newDate);
            }
        };

        // 数据初始化
        vm.getKeyIndexByDate(vm.date);
        vm.getSessionDistributionByDate(vm.date);
        vm.getCategoryDistributionByDate(vm.date);
        vm.getSearchEngineContributionByDate(vm.date);
        vm.getCountriesContributionByDate(vm.date);
        vm.getMainLandingCategoriesByDate(vm.date);
        vm.getMainDropOffCategoriesByDate(vm.date);
        vm.getHotPagesByDate(vm.date);
    });