angular.module('vislog.path', ['chart.js'])

    .controller('PathCtrl', ['$http', 'baseUrl', function ($http, baseUrl) {

        var vm = this;

        //vm.pathData = sessionPathData.pathData;

        var clockFlag = false;

        // 请求的进度信息
        vm.progressData = 10;
        vm.isGraphBtnActive = false;

        // 表单上传数据
        vm.postData = {
            startDate: '2015-07-01',
            endDate: '2015-07-02',
            pathWeight: 4.5
        };

        // 计时器配置项
        vm.displayOption = {
            progressInfo: 0,
            isDisabled: true // 默认禁用按钮
        };

        var timeInterval = setInterval(updateClock, 1000);

        // 获取用户路径数据
        vm.getFlowPath = function (postData) {

            var config = {
                headers: {
                    'content-type': 'application/json',
                    'accept': 'application/json'
                }
            };

            var requestUrl = baseUrl + 'sessions/path/flow?'
                + "startDate=" + postData.startDate
                + "&endDate=" + postData.endDate
                + "&pathWeight=" + postData.pathWeight;

            $http.get(requestUrl, config)
                .success(function (response) {
                    if (response.result === 'success') {
                        // 图片文件生成成功
                        vm.displayOption.isDisabled = false;


                    } else {
                        // 返回失败结果

                    }
                    stopClock();
                })
                .catch(function (err) {
                    if (err) throw err;
                });
        };

        // 响应按钮事件：根据表单事件获取行的用户路径数据
        vm.handleNewUserFlowPath = function (postData) {

            startClock();

            if (angular.isDefined(postData)) {
                vm.getFlowPath(postData);
            }
        };


        vm.orderedAttribute = null;

        vm.clickedNode = {
            labels: ["Through", "Drop-off"],
            in_degree: 1,
            out_degree: 1
        };

        vm.orderBy = function (item) {
            vm.orderedAttribute = item;
        };

        function startClock() {
            clockFlag = false;

            var t = 0;

            function updateClock() {
                t++;
                vm.displayOption.progressInfo = t;
                if (clockFlag) {
                    clearInterval(timeInterval);
                }
            }

            updateClock();
            var timeInterval = setInterval(updateClock, 1000);
        }

        function stopClock() {
            clockFlag = true;
        }

    }]);