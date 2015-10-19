angular.module('vislog.path', ['chart.js'])

    //.controller('PathCtrl', ['$http', 'baseUrl', function ($http, baseUrl) {
    .controller('PathCtrl', [function () {

        var vm = this;

        vm.errorMsg = '';
        vm.selectedGraph = 'data/2015-07-01.json';

        vm.clickedNode = {
            labels: ["Through", "Drop-off"],
            in_degree: 1,
            out_degree: 1
        };

        vm.graphList = [
            {path: 'data/session-path.json', date: '2014-10-24'},
            {path: 'data/2015-07-01.json', date: '2015-07-01'},
            {path: 'data/2015-07-02.json', date: '2015-07-02'}

        ];

        vm.refreshGraphBtn = function () {
            var graphWrapper = $('#path-chart')[0];
            graphWrapper.append('<div>test</div>');
            //var pathGraph = angular.element('<path-chart data="pathCtrl.selectedGraph" name="url" width="800" height="600" node-label="name" clicked-point="pathCtrl.clickedNode"></path-chart>')

            //var pathGraphString = '<path-chart data="pathCtrl.selectedGraph" name="url" width="800" height="600" node-label="name" clicked-point="pathCtrl.clickedNode"></path-chart>';

            //graphWrapper.innerHTML = '';
            //graphWrapper.append("<div>haha</div>");
            //graphWrapper.append(pathGraphString);
        };

        //vm.pathData = sessionPathData.pathData;
        //var clockFlag = false;

        //vm.isGraphBtnActive = false;
        //
        //// 表单上传数据
        //vm.postData = {
        //    startDate: '2015-07-01',
        //    endDate: '2015-07-02',
        //    graphDepth: 7,
        //    pathWeight: 4.5
        //};
        //
        //// 计时器配置项
        //vm.displayOption = {
        //    progressInfo: 0,
        //    isDisabled: true // 默认禁用按钮
        //};
        //
        //// 获取用户路径数据
        //vm.getFlowPath = function (postData) {
        //
        //    var config = {
        //        headers: {
        //            'content-type': 'application/json',
        //            'accept': 'application/json'
        //        }
        //    };
        //
        //    var requestUrl = baseUrl + 'sessions/path/flow?'
        //        + "startDate=" + postData.startDate
        //        + "&endDate=" + postData.endDate
        //        + "&graphDepth=" + postData.graphDepth
        //        + "&pathWeight=" + postData.pathWeight;
        //
        //    $http.get(requestUrl, config)
        //        .success(function (response) {
        //            if (response.result === 'success') {
        //                // 图片文件生成成功
        //                vm.displayOption.isDisabled = false;
        //            } else {
        //                // 返回失败结果
        //                vm.displayOption.isDisabled = true;
        //                vm.errorMsg = 'Encounter server side error!';
        //            }
        //            stopClock();
        //        })
        //        .catch(function (err) {
        //
        //            if (err) throw err;
        //        });
        //};
        //
        //// 响应按钮事件：根据表单事件获取行的用户路径数据
        //vm.handleNewUserFlowPath = function (postData) {
        //
        //    startClock(); // 启动计时器
        //
        //    if (angular.isDefined(postData)) {
        //        vm.getFlowPath(postData); // 将参数信息发送到服务端，计算行的图
        //    }
        //};
        //
        //// 响应显示新的图谱：根据新生成的数据展示到当前界面中
        //vm.handleDisplayNewGraph = function () {
        //    vm.displayOption.progressInfo = 0;
        //    vm.graphPath = 'data/' + vm.postData.startDate + '.json' || 'data/session-path.json';
        //};
        //
        //
        //vm.orderedAttribute = null;
        //

        //
        //vm.orderBy = function (item) {
        //    vm.orderedAttribute = item;
        //};
        //
        //function startClock() {
        //    clockFlag = false;
        //
        //    var t = 0;
        //
        //    function updateClock() {
        //        t++;
        //        vm.displayOption.progressInfo = t;
        //        if (clockFlag) {
        //            clearInterval(timeInterval);
        //        }
        //    }
        //
        //    updateClock();
        //    var timeInterval = setInterval(updateClock, 1000);
        //}
        //
        //function stopClock() {
        //    clockFlag = true;
        //}

    }]);