'use strict';

var filterServices = angular.module('hiveWebApp.services.filterServices', []);

filterServices.factory('HttpInterceptor', ['$window', '$location', '$q', '$injector',
    function ($window, $location, $q, $injector) {
        return {
            request: function(config) {
                return config;
            },

            requestError: function(rejection) {
                return $q.reject(rejection);
            },

            response: function(response) {
                return response || $q.when(response);
            },

            responseError: function(rejection) {
                $window = $window || $injector.get('$window');
                if (rejection.status === 0) {
                    alertError('SSO可能超时或已登出', '如需继续使用，请：<br />1. 点击<a href="' + window.location.href + '" target="_blank">确定</a><br />2. 在新页面进行登录<br />3. 回到当前页面继续操作');
//                    var res = confirm('系统检测到用户已在别的页面登出了SSO\n如需继续使用本页面，请\n1. 点击确定\n2. 在新页面登录\n3. 回到当前页面继续操作');
//                    if (res) {
//                        window.open(window.location.href);
//                    }
                } else {
                    // TODO alert error
                }
                return $q.reject(rejection);
            }
        };
    
    }
]);