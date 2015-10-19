angular.module("vislog", [
    'ui.router',
    'chart.js',

    'vislog.appRoutes',

    'vislog.overview',
    'vislog.path'
])
    .constant('baseUrl', 'http://localhost:8080/vislog_restful/api/');