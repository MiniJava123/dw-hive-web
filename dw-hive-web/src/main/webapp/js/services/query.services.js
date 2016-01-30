'use strict';

/* Services */

var queryServices = angular.module('hiveWebApp.services.queryServices', [ 'ngResource' ]);

queryServices.factory('Query', function ($http) {
    return {
        submit: function (data) {
            return $http({
                method: 'POST',
                url: 'rest/query/submit',
                data: data
            });
        }
    }
});

queryServices.factory('Reply', function ($http) {
    return {
        submit: function (data) {
            return $http({
                method: 'POST',
                url: 'rest/query/reply',
                data: data
            });
        }
    }
});
