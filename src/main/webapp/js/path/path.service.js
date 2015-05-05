angular.module('vislog.path')
    .factory('sessionPathData', ['$http', function ($http) {
        var obj = {
            pathData: []
        };

        obj.getPathData = function () {

            // todo: get data via RESTful services
            // todo: improve the computation efficiency -- path service

            return $http.get('./data/session-path.json', {
                header: {'Content-Type': 'application/json; charset:utf-8'}
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