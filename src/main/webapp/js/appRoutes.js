angular.module('vislog.appRoutes', [])

    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/');

        $stateProvider
            .state('overview', {
                url: '/',
                templateUrl:'views/overview.html',
                controller: 'OverviewCtrl as overviewCtrl'
            })

            .state('path', {
                url: '/path',
                templateUrl:'views/path.html',
                controller: 'PathCtrl as pathCtrl',
                resolve: {
                    pathDataPromise: ['sessionPathData', function(sessionPathData) {
                        return sessionPathData.getPathData();
                    }]
                }
            });

    });