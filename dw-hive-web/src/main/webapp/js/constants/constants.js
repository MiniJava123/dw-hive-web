'use strict';

angular.module('hiveWebApp.constants', [])
    .constant('QUERY_STATUS', {
        'READY': 'READY',
        'RUNNING': 'RUNNING',
        'STOPPED': 'STOPPED',
        'FINISHED': 'FINISHED',
        'TIMEOUT': 'TIMEOUT',
        'FAILED': 'FAILED'
    })
    .constant('QUERY_STATUS_CLASS', {
        'READY': 'icon-stop light-grey',
        'RUNNING': 'icon-play blue',
        'STOPPED': 'icon-stop light-grey',
        'FINISHED': 'icon-ok green',
        'TIMEOUT': 'icon-exclamation red',
        'FAILED': 'icon-exclamation red'
    })
    .constant('QUERY_STATUS_COLOR', {
        'READY': 'light-grey',
        'RUNNING': 'blue',
        'STOPPED': 'light-grey',
        'FINISHED': 'green',
        'TIMEOUT': 'red',
        'FAILED': 'red'
    })
    .constant('USER_PERSONALIZATION_TYPE', {
        'DATABASE': 'DATABASE',
        'QUERY_ENGINE': 'QUERY_ENGINE',
        'ROLE': 'ROLE'
    });
