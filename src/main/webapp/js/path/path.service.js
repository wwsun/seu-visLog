angular.module('vislog.path')
    .factory('sessionPathData', ['$http', function ($http) {
        var obj = {
            pathData: []
        };

        obj.getPathData = function () {
            return $http.get('./data/flow.json', {
                header: {'Content-Type': 'application/json; charset:GBK'}
            })
                .success(function (data) {
                    angular.copy(data, obj.pathData);
                })
                .error(function (data) {
                    console.log(data);
                });
        };

        return obj;
    }]);