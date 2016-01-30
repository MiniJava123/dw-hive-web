'use strict';

// Declare app level module which depends on filters, and services
var hiveWebApp = angular.module('hiveWebApp', [
    'ngRoute',

    'ui.bootstrap',
    'ui.bootstrap.alert',
    'ui.codemirror',

    'hiveWebApp.constants',
    'hiveWebApp.filters',
    'hiveWebApp.query.controllers',
    'hiveWebApp.queryHistory.controllers',
    'hiveWebApp.directives',
    'hiveWebApp.services.filterServices',
    'hiveWebApp.create.table.controllers',
    'hiveWebApp.services.createTableServices',
    'hiveWebApp.feedback.controllers'
]);

hiveWebApp.config(['$routeProvider', '$locationProvider', '$httpProvider',
   function($routeProvider, $locationProvider, $httpProvider) {
        // +路由规则
        $routeProvider
            .when('/query', {
                templateUrl: 'pages/query.html'
            })
            .when('/query-history', {
                templateUrl: 'pages/query-history.html'
            })
            .when('/create-table', {
                templateUrl: 'pages/create-table.html'
            })
            .when('/help', {
                templateUrl: 'pages/help.html'
            })
            .when('/feed-back', {
                templateUrl: 'pages/feedback.html'
            })
            .otherwise({
                redirectTo: '/query'
            });

        $httpProvider.interceptors.push('HttpInterceptor');

        // use the HTML5 History API
//        $locationProvider.html5Mode(true);
    }
]);
