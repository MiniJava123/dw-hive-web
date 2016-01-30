'use strict';

/* Services */

var queryServices = angular.module('hiveWebApp.services.createTableServices', [ 'ngResource' ]);

queryServices.factory('TmpTable', function($http) {
    return {
        buildCreateTableSQL: function(data) {
            return $http({
                method: 'POST',
                url: 'rest/tmpTable/createSql',
                data: data
            });
        },
        createTable: function(data) {
            return $http({
                method: 'POST',
                url: 'rest/tmpTable/createTable',
                data: data
            })
        },
        loadDataIntoTmpTable: function(data) {
            return $http({
                method: 'POST',
                url: 'rest/tmpTable/loadData',
                data: data
            })
        }
    }
});
