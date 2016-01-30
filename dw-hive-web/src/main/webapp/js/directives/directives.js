'use strict';

angular.module('hiveWebApp.directives', [ 'hiveWebApp.services.environmentServices' ])
    .directive('loginAvatar', function() {
        return {
            restrict: 'E',
            templateUrl: 'pages/login-avatar.html',
            controller: function($scope, $http, $window) {
                $http.get('rest/currentUser/employeeCnName').success(function(data) {
                    $scope.loginUserName = data;
                });
    
                $scope.window = $window;

                $scope.logout = function() {
                    $scope.window.location.href = 'rest/logout';
                };
            }
        };
    })
    .directive('sidebar', function() {
        return {
            restrict: 'A',
            templateUrl: 'pages/sidebar.html',
            controller: function($scope, $location) {
                if ($location.path() == '/query') {
                    $scope.queryActive = 'active';
                    $scope.queryHistoryActive = '';
                    $scope.createTableActive = '';
                    $scope.helpActive = '';
                    $scope.feedbackActive = '';
                } else if ($location.path() == '/query-history') {
                    $scope.queryActive = '';
                    $scope.queryHistoryActive = 'active';
                    $scope.createTableActive = '';
                    $scope.helpActive = '';
                    $scope.feedbackActive = '';
                } else if ($location.path() == '/create-table') {
                    $scope.queryActive = '';
                    $scope.queryHistoryActive = '';
                    $scope.createTableActive = 'active';
                    $scope.helpActive = '';
                    $scope.feedbackActive = '';
                } else if ($location.path() == '/help') {
                    $scope.queryActive = '';
                    $scope.queryHistoryActive = '';
                    $scope.createTableActive = '';
                    $scope.helpActive = 'active';
                    $scope.feedbackActive = '';
                } else if ($location.path() == '/feed-back') {
                    $scope.queryActive = '';
                    $scope.queryHistoryActive = '';
                    $scope.createTableActive = '';
                    $scope.helpActive = '';
                    $scope.feedbackActive = 'active';
                };
                
                $scope.change_help_state = function () {
                    $scope.queryActive = '';
                    $scope.queryHistoryActive = '';
                    $scope.createTableActive = '';
                    $scope.helpActive = 'active';
                    $scope.feedbackActive = '';
                };
                $scope.change_feedback_state = function () {
                    $scope.queryActive = '';
                    $scope.queryHistoryActive = '';
                    $scope.createTableActive = '';
                    $scope.helpActive = '';
                    $scope.feedbackActive = 'active';
                }
            }
        };
    });
