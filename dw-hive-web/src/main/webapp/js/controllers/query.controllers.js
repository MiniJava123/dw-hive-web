'use strict';

var queryControllers = angular.module('hiveWebApp.query.controllers', [
        'hiveWebApp.constants',
        'hiveWebApp.services.environmentServices',
        'hiveWebApp.services.queryServices',
        'hiveWebApp.services.keyboardManagerServices'
]);

queryControllers.controller('queryCtrl', [
        '$scope', '$http', '$modal', '$window', '$timeout', 
        'Database', 'QueryEngine', 'Query', 'Role', 'KeyboardManager',
        'QUERY_STATUS', 'QUERY_STATUS_CLASS',
function($scope, $http, $modal, $window, $timeout, Database, QueryEngine, Query, Role, KeyboardManager, QUERY_STATUS, QUERY_STATUS_CLASS) {
    var tabId = 0;

    $scope.rowLimits = [100000, 500000, 1000000, 10000000];

    Database.getAll().success(function(data) {
        $scope.allDatabases = data;
    });
    Database.getDefault().success(function(data) {
        $scope.selectedDatabase = data;
    });

    QueryEngine.getAll().success(function(data) {
        $scope.allQueryEngines = data;
    });
    QueryEngine.getDefault().success(function(data) {
        $scope.selectedQueryEngine = data;
    });

    Role.getAll().success(function(data) {
        $scope.allQueryRoles = data;
        if (data.length === 0) {
            alertNoRole();
        }
    });
    Role.getDefault().success(function (data) {
        $scope.selectedQueryRole = data;
    });

    $scope.saveDatabase = function(value) {
        Database.save(value);
    };
    $scope.saveQueryEngine = function(value) {
        QueryEngine.save(value);
    };
    $scope.saveRole = function(value) {
        Role.save(value);
    };

    KeyboardManager.bind('ctrl+enter', function() {
        var tabList = $scope.tabList;
        for (var idx in tabList) {
            var tab = tabList[idx];
            if (tab.tabActive) {
                $scope.runQuery(tab);
                return;
            }
        }
    });

    // 查询Tab列表
    $scope.tabList = [];

    // 添加新的查询
    $scope.addTab = function() {
        $scope.tabList.push({
            tabId: tabId,
            tabTitle: '查询-' + tabId,
            tabActive: true,
            retryLogButton: false,
            execButton: true,
            stopButton: false,
            statusClass: QUERY_STATUS_CLASS.READY,
            resultTabs: [],
            currentQueryId: '',
            rowLimit: $scope.rowLimits[0]
        });
        tabId++;
    };
    $scope.addTab();

    $scope.codemirrorLoaded = function(cm) {
        // important! don't remove timeout function!
        $timeout(function() {
            $scope.tabList[$scope.tabList.length - 1].cmDoc = cm.getDoc();
            cm.setOption('mode', 'text/x-hive');
            cm.setOption('styleActiveLine', true);
            cm.setOption('lineNumbers', true);
            cm.setOption('lineWrapping', true);
            cm.setOption('themes', 'cm-s-default');
            cm.setOption('extraKeys', { 'Alt-/': 'autocomplete' });
            cm.focus();
        }, 100);
    };

    // 关闭查询 Tab页
    $scope.closeTab = function(tabIndex) {
        var modalInstance = $modal.open({
            templateUrl: 'pages/close-tab.html',
            controller: function($scope, $modalInstance) {
                $scope.ok = function(status) {
                    $modalInstance.close(true);
                };

                $scope.cancel = function(status) {
                    $modalInstance.close(false);
                };
            }
        });
        modalInstance.result.then(function(status) {
            if (status) {
                $scope.tabList.splice(tabIndex, 1);
            }
        });
    };

    // 开始执行查询
    $scope.runQuery = function(currentTab) {
        var uuid = newUuid();
        currentTab.uuid = uuid;
        currentTab.retryLogButton = false;
        currentTab.execButton = false;
        currentTab.stopButton = true;
        currentTab.statusClass = QUERY_STATUS_CLASS.READY;
        currentTab.resultTabs = [];
        currentTab.currentQueryId = '';

        var queryList = generateQueryList(currentTab);
        currentTab.queryList = queryList;
        if (queryList.length == 0) {
            alertWarning('请输入SQL...', '【' + currentTab.tabTitle + '】请输入SQL...！');
            currentTab.retryLogButton = false;
            currentTab.execButton = true;
            currentTab.stopButton = false;
            return;
        }

        if (!$scope.selectedDatabase && $scope.selectedDatabase != ' ') {
            alertWarning('请选择数据库...');
            currentTab.retryLogButton = false;
            currentTab.execButton = true;
            currentTab.stopButton = false;
            return;
        }
        if (!$scope.selectedQueryEngine && $scope.selectedQueryEngine != ' ') {
            alertWarning('请选择执行引擎...');
            currentTab.retryLogButton = false;
            currentTab.execButton = true;
            currentTab.stopButton = false;
            return;
        }
        if (!$scope.selectedQueryRole && $scope.selectedQueryRole != ' ') {
            alertWarning('请选择查询角色...');
            currentTab.retryLogButton = false;
            currentTab.execButton = true;
            currentTab.stopButton = false;
            return;
        }

        // 发送查询请求
        $scope.sendSqlQueryRequest(uuid, currentTab, queryList, 0);
    };

    $scope.sendSqlQueryRequest = function(uuid, currentTab, queryList, queryIdx) {
        if (currentTab.uuid != uuid || !existsTab(currentTab)) {
            return;
        }
        var currentQuery = queryList[queryIdx];
        Query.submit({
        	application: 'hive-web',
            databaseName: currentQuery.databaseName,
            execMode: currentQuery.execMode,
            roleName: currentQuery.roleName,
            hql: currentQuery.hql,
            rowLimit: currentQuery.rowLimit,
            properties: currentQuery.properties
        }).then(
            function(response) {
                if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                    return;
                }
                currentTab.currentQueryId = response.data;
                currentQuery.queryId = response.data;
                startPullQueryLogAndResult(uuid, currentTab, queryList, queryIdx);
                console.log('【' + currentTab.tabTitle + '】【' + queryIdx + '】提交查询成功！')
            },
            function(reason) {
                if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                    return;
                }
                currentTab.currentQueryId = '';
                currentQuery.queryId = '';
                currentTab.retryLogButton = true;
                currentTab.execButton = true;
                currentTab.stopButton = false;
                alertError('提交查询异常...', '【' + currentTab.tabTitle + '】提交查询异常！');
                console.log('【' + currentTab.tabTitle + '】【' + queryIdx + '】提交查询失败！')
            }
        );
    };

    // 开始轮询每个查询的log & result
    function startPullQueryLogAndResult(uuid, currentTab, queryList, queryIdx) {
        if (currentTab.uuid != uuid || !existsTab(currentTab)) {
            return;
        }
        $timeout(function() {
            if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                return;
            }
            var currentQueryId = queryList[queryIdx].queryId;
            $http({
                method: 'GET',
                url: 'rest/query/result/' + currentQueryId
            }).then(
                function(response) {
                    if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                        return;
                    }
                    var responseData = response.data;
                    var queryStatus = responseData.queryStatus;

                    switch (queryStatus) {
                    case QUERY_STATUS.RUNNING:
                        currentTab.statusClass = QUERY_STATUS_CLASS.RUNNING;
                    case QUERY_STATUS.READY:
//                        startPullQueryLogAndResult(uuid, currentTab, queryList, queryIdx);
//                        break;
                        var queryLog = responseData.queryLog;
                        if (!currentTab.resultTabs[queryIdx * 2]) {
                            currentTab.resultTabs[queryIdx * 2] = {
                                type: 'log',
                                title: '日志-' + queryIdx,
                                parentTabId: currentTab.tabId,
                                queryIdx: queryIdx,
                                emptyColumn: false,
                                icon: 'icon-file-text'
                            };
                        }
                        currentTab.resultTabs[queryIdx * 2].log = queryLog;
                        setExecInfoForLog(currentTab.resultTabs, queryList, queryIdx);

                        scrollButtomLog(currentTab.tabId, queryIdx);

                        startPullQueryLogAndResult(uuid, currentTab, queryList, queryIdx);
                        break;
                    case QUERY_STATUS.STOPPED:
                        break;
                    case QUERY_STATUS.FINISHED:
                        currentTab.statusClass = QUERY_STATUS_CLASS.RUNNING;

                        var queryLog = responseData.queryLog;
                        if (!currentTab.resultTabs[queryIdx * 2]) {
                            currentTab.resultTabs[queryIdx * 2] = {
                                type: 'log',
                                title: '日志-' + queryIdx,
                                parentTabId: currentTab.tabId,
                                queryIdx: queryIdx,
                                emptyColumn: false,
                                icon: 'icon-file-text'
                            };
                        }
                        currentTab.resultTabs[queryIdx * 2].log = queryLog;
                        setExecInfoForLog(currentTab.resultTabs, queryList, queryIdx);

                        scrollButtomLog(currentTab.tabId, queryIdx);

                        var resultData = responseData.data;
                        var resultFilePath = responseData.resultFilePath;
                        var csvPath = responseData.csvPath;
                        var xlsxPath = responseData.xlsxPath;
                        var columnNames = responseData.columnNames;

                        var emptyColumn = !columnNames || (columnNames.length == 0);
                        currentTab.resultTabs[queryIdx * 2 + 1] = {
                            type: 'result',
                            title: '结果-' + queryIdx,
                            tabActive: true,
                            parentTabId: currentTab.tabId,
                            queryId: responseData.queryId,
                            queryIdx: queryIdx,
                            emptyColumn: emptyColumn,
                            icon: 'icon-download-alt',
                            resultFilePath: resultFilePath,
                            csvPath: csvPath,
                            xlsxPath: xlsxPath
                        };

                        if (!emptyColumn) {
                            if (!resultData) {
                                resultData = [];
                            }
                            for (var i = 0; i < resultData.length; ++i) {
                                for (var j = 0; resultData[0] && j < resultData[0].length; ++j) {
                                    resultData[i][j] = _.escape(resultData[i][j]);
                                }
                            }
                            var columnDefs = [];
                            angular.forEach(columnNames, function(value) {
                                columnDefs.push({
                                    title: value
                                });
                            });
    
                            $timeout(function() {
                                var id = '#data-tables-' + currentTab.tabId + '-' + queryIdx;
                                var dt = 
                                    $(id).dataTable({
                                        'language': {
                                            url: 'assets/language/jquery.dataTables.zh_CN.txt'
                                        },
                                        data: resultData,
                                        columns: columnDefs,
//                                        columnDefs: [{
//                                            type: 'chinese-string',
//                                            targets: 0
//                                        }],
                                        aaSorting: [],
                                        dom: 'T<"clear">lfr<"dt-scroll"t>ip',
                                        iDisplayLength: 25,
                                        tableTools: {
                                            'aButtons': [{
                                                'sExtends': 'copy',
                                                'sButtonText': '复制到剪贴板',
                                                'fnComplete': function(nButton, oConfig, flash, text) {
                                                    var lines = text.split('\n').length;
                                                    if (oConfig.bHeader) lines--;
                                                    if (this.s.dt.nTFoot !== null && oConfig.bFooter) lines--;
                                                    alert('已复制' + lines + '行到剪贴板中');
                                                }
                                            }],
                                            'sSwfPath': 'assets/swf/copy_csv_xls_pdf.swf'
                                        }
                                    });
                                if ($(id).width() > $(id).parent().width()) {
                                    $(id).addClass('table-cell-max-width');
                                }
                            }, 500);
                        }

                        if (queryIdx === queryList.length - 1) {
                            currentTab.statusClass = QUERY_STATUS_CLASS.FINISHED;
                            currentTab.retryLogButton = false;
                            currentTab.execButton = true;
                            currentTab.stopButton = false;
                            alertSuccess('SQL执行完毕', '【' + currentTab.tabTitle + '】SQL执行成功！');
                        } else {
                            $scope.sendSqlQueryRequest(uuid, currentTab, queryList, queryIdx + 1);
                        }
                        console.log('【' + currentTab.tabTitle + '】【' + queryIdx + '】SQL执行成功！');
                        break;
                    case QUERY_STATUS.TIMEOUT:
                    case QUERY_STATUS.FAILED:
                        currentTab.statusClass = QUERY_STATUS_CLASS.FAILED;
                        currentTab.retryLogButton = true;
                        currentTab.execButton = true;
                        currentTab.stopButton = false;

                        var queryLog = responseData.queryLog;
                        if (!currentTab.resultTabs[queryIdx * 2]) {
                            currentTab.resultTabs[queryIdx * 2] = {
                                type: 'log',
                                title: '日志-' + queryIdx,
                                parentTabId: currentTab.tabId,
                                queryIdx: queryIdx,
                                emptyColumn: false,
                                icon: 'icon-file-text'
                            };
                        }
                        currentTab.resultTabs[queryIdx * 2].log = queryLog;
                        setExecInfoForLog(currentTab.resultTabs, queryList, queryIdx);

                        scrollButtomLog(currentTab.tabId, queryIdx);

                        alertError('SQL执行失败', '【' + currentTab.tabTitle + '】SQL执行失败！');
                        console.log('【' + currentTab.tabTitle + '】【' + queryIdx + '】SQL执行失败！');
                    default:
                        break;
                    }
                },
                function(reason) {
                    if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                        return;
                    }
                    currentTab.statusClass = QUERY_STATUS_CLASS.FAILED;
                    currentTab.retryLogButton = true;
                    currentTab.execButton = true;
                    currentTab.stopButton = false;
                    alertError('SQL执行异常', '【' + currentTab.tabTitle + '】SQL执行异常！');
                }
            );
        }, 3000);
    };

    // 停止查询
    $scope.stopQuery = function(currentTab) {
        var uuid = '';
        currentTab.uuid = uuid;
        currentTab.statusClass = QUERY_STATUS_CLASS.STOPPED;
        currentTab.retryLogButton = false;
        currentTab.execButton = true;
        currentTab.stopButton = false;
        var queryId = currentTab.currentQueryId;
        if (queryId) {
            $http({
                method: 'GET',
                url: 'rest/query/stop/' + queryId
            }).then(
                function(response) {
                    if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                        return;
                    }
                    console.log('【' + currentTab.tabTitle + '】停止查询成功！')
                },
                function(reason) {
                    if (currentTab.uuid != uuid || !existsTab(currentTab)) {
                        return;
                    }
                    console.log('【' + currentTab.tabTitle + '】停止查询失败！')
                }
            )
        }
    };

    // 重新获取日志
    $scope.retryLog = function(currentTab) {
    	startPullQueryLogAndResult(currentTab.uuid, currentTab, currentTab.queryList, 0);
    	currentTab.retryLogButton = false;
    	currentTab.execButton = false;
        currentTab.stopButton = true;
        currentTab.statusClass = QUERY_STATUS_CLASS.READY;
    };

    // 下载结果
    $scope.downloadCsv = function(queryIdx) {
        $window.open('rest/query/download/csv/' + queryIdx);
    };

    $scope.downloadXlsx = function(queryIdx) {
        $window.open('rest/query/download/xlsx/' + queryIdx);
    };

    // patch to TableTools
    $scope.renderTab = function(resultTab) {
        if (resultTab.type == 'result') {
            var ttInstances = TableTools.fnGetMasters();
            for (var i in ttInstances) {
                if (ttInstances[i].fnResizeRequired()) {
                    ttInstances[i].fnResizeButtons();
                }
            }
            var id = '#data-tables-' + resultTab.parentTabId + '-' + resultTab.queryIdx;
            if ($(id).width() > $(id).parent().width()) {
                $(id).addClass('table-cell-max-width');
            }
        }
    };

    function generateQueryList(tab) {
        var ret = [];
        var createTempFunc = '';

        var databaseName = $scope.selectedDatabase;
        var execMode = $scope.selectedQueryEngine;
        var roleName = $scope.selectedQueryRole;
        var rowLimit = tab.rowLimit;

        var hqls = fetchHqls(tab);
        // 匹配 hqls 里的 \;，如果有就暂时先替换为特殊字符 \u1111
        var reg = new RegExp('\\\\;', 'gi');
        hqls = hqls.replace(reg, '\u1111');

        // 解析hqls为多个hql
        var hqlArr = hqls.split(';');
        var properties = '';
        angular.forEach(hqlArr, function(hql) {
            hql = hql.trim();
            if (hql) {
                var pattern = new RegExp('^set', 'i'); // case insensitive
                if (hql.match(pattern)) { // 匹配set key=value;语句
                    var kvs = hql.split('=');
                    var words = kvs[0].trim().split(' ');
                    var key = words[words.length - 1].trim();
                    var value = kvs[1].trim();
                    properties += key + ':' + value + ',';
                } else {
                    // 如果有之前的替换字符，则再替换回来
                    hql = hql.replace(/\u1111/g, '\\;');
                    
                    // 判断是否是创建tmp udf语句，如果是暂存起来
                    var pattern2 = new RegExp('^create temporary function', 'i');
                    if (hql.match(pattern2)) {
                        createTempFunc = hql;
                    } else {
                        // 如果之前有创建tmp udf的语句，与当前hql一起组装起来
                        if (createTempFunc) {
                            hql = createTempFunc + ';' + hql;
                        }
                        ret.push({
                            databaseName: databaseName,
                            execMode: execMode,
                            roleName: roleName,
                            rowLimit: rowLimit,
                            queryId: '',
                            hql: hql,
                            properties: properties == '' ? '' : properties.substring(0, properties.length - 1)
                        });
                    }
                }
            }
        });
        return ret;
    }

    function fetchHqls(tab) {
        // 获取CM内容
        var selectCodeContent = tab.cmDoc.getSelection();
        if (selectCodeContent) {
            return selectCodeContent.trim();
        } else {
            return tab.cmDoc.getValue().trim();
        }
    }

    function alertNoRole() {
        var modalInstance = $modal.open({
            templateUrl: 'pages/has-no-role.html',
            controller: function($scope, $modalInstance) {
                $scope.ok = function() {
                    $http({
                        method: 'GET',
                        url: 'rest/logout'
                    });
                    $modalInstance.close();
                };
            }
        });
        modalInstance.result.then(function() {
            $scope.window.location.href = 'http://auth.data.dp';
        });
    }

    function scrollButtomLog(tabId, queryIdx) {
        $timeout(function() {
            var obj = $('#result-log-' + tabId + '-' + queryIdx);
            obj.scrollTop(obj[0].scrollHeight);
        }, 200);
    }

    function existsTab(tab) {
        var ok = false;
        for (var i = 0; i < $scope.tabList.length; ++i) {
            if ($scope.tabList[i].tabId === tab.tabId) {
                ok = true;
            }
        }
        return ok;
    }

    function setExecInfoForLog(resultTab, queryList, queryIndex) {
        if (!resultTab[queryIndex * 2].hasOwnProperty('execInfo')) {
            resultTab[queryIndex * 2].execInfo = {
                roleName: queryList[queryIndex].roleName,
                databaseName: queryList[queryIndex].databaseName,
                execMode: queryList[queryIndex].execMode,
                properties: queryList[queryIndex].properties,
                hql: queryList[queryIndex].hql
            };
        }
    }
} ]);

