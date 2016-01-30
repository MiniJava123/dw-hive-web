'use strict';

/* Services */

var environmentServices = angular.module('hiveWebApp.services.environmentServices', []);

environmentServices.factory('Database', function ($http, USER_PERSONALIZATION_TYPE) {
    return {
        getAll: function () {
            return $http({
                method: 'GET',
                url: 'rest/database'
            });
        },
        getDefault: function () {
            return $http({
                method: 'GET',
                url: 'rest/database/default'
            });
        },
        save: function (value) {
            return $http({
                method: 'POST',
                url: 'rest/database/save',
                data: { type: USER_PERSONALIZATION_TYPE.DATABASE, value: value}
            })
        }
    };
});

environmentServices.factory('QueryEngine', function ($http, USER_PERSONALIZATION_TYPE) {
    return {
        getAll: function () {
            return $http({
                method: 'GET',
                url: 'rest/queryEngine'
            });
        },
        getDefault: function () {
            return $http({
                method: 'GET',
                url: 'rest/queryEngine/default'
            });
        },
        save: function(value) {
            return $http({
                method: 'POST',
                url: 'rest/queryEngine/save',
                data: { type: USER_PERSONALIZATION_TYPE.QUERY_ENGINE, value: value }
            })
        }
    };
});

environmentServices.factory('Role', function ($http, USER_PERSONALIZATION_TYPE) {
    return {
        getAll: function() {
            return $http({
                method: 'GET',
                url: 'rest/role'
            });
        },
        getDefault: function() {
            return $http({
                method: 'GET',
                url: 'rest/role/default'
            });
        },
        save: function (value) {
            return $http({
                method: 'POST',
                url: 'rest/role/save',
                data: { type: USER_PERSONALIZATION_TYPE.ROLE, value: value}
            })
        }
    };
});
