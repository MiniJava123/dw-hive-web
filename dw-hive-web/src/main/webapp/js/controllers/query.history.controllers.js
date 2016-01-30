'use strict';

var queryHistoryModule = angular.module('hiveWebApp.queryHistory.controllers', [
        'hiveWebApp.constants'
]);

var queryHistoryFunc;

queryHistoryModule.controller('sqlQueryHistoryCtrl', [
    '$scope', '$http', '$timeout', '$filter', 'QUERY_STATUS', 'QUERY_STATUS_COLOR', 
        function ($scope, $http, $timeout, $filter, QUERY_STATUS, QUERY_STATUS_COLOR) {

            $scope.columnDefs = [
//                {title: '角色'},
                {title: '提交时间'},
                {title: '查询语句'},
                {title: '方式'},
                {title: '状态'},
                {title: '耗时'},
                {title: '下载',
                    mRender: function (data, type, full) {
                    	return data.charAt(0) == '1' ? 
                    			('<a class="download-link" target="_blank" href="rest/query/download/csv/' + data.substr(1) + 
                    					'">&nbsp;下载CSV</a>&nbsp;<a class="download-link" target="_blank" href="rest/query/download/xlsx/' + data.substr(1) + '">&nbsp;下载Excel</a>') : (
                    							data.charAt(0) == '2' ? 
                    	                    			('<a class="download-link" target="_blank" href="rest/query/download/csv/' + data.substr(1) + '">&nbsp;下载CSV</a>') : (
                    	                    					data.charAt(0) == '3' ? 
                    	                                    			('<a class="download-link" target="_blank" href="rest/query/download/xlsx/' + data.substr(1) + '">&nbsp;下载Excel</a>') : '')
                    						);
                    }
                }
            ];

            $scope.renderDataTables = function (resultData, columnDefs) {
                for (var i = 0; i < resultData.length; ++i) {
                    if (resultData[i]) {
                        resultData[i][1] = '<pre class="history-pre">' + _.escape(resultData[i][1]) + '</pre>';
                        resultData[i][2] = resultData[i][2] ? resultData[i][2].toLowerCase() : '';
                        resultData[i][3] = parseStatus(resultData[i][3]);
                        resultData[i][4] = resultData[i][4] == -1 ? '-' : resultData[i][4];
                    }
                }
                $timeout(function () {
                    $('#data-tables-query-history')
                        .dataTable({
                            'language': {
                                url: 'assets/language/jquery.dataTables.zh_CN.txt'
                            },
                            destroy: true,
                            data: resultData,
                            columns: columnDefs,
                            aaSorting: [],
                            dom: 'lfr<"dt-scroll"t>ip',
                            iDisplayLength: 25
                        })
                        .$('tr').each(function () {
                            var nTds = $('td', this);
                            var sTitle = $(nTds[1]).text();
                            nTds[1].setAttribute('title', sTitle);
                        });
                    $scope.renderTab();
                }, 500);
            };

            // patch to TableTools
            $scope.renderTab = function () {
                $('#data-tables-query-history').addClass('table-cell-max-width');
                $('#data-tables-query-history tbody tr td[title]').tooltip({
                    delay: 0,
                    track: true,
                    fade: 250
                });
            };

            queryHistoryFunc = function(keyword) {
            	$http({
                    method: 'POST',
                    url: 'rest/query/history',
                    data: { keyword: keyword }
                }).then(
                    function (respone) {
                        $scope.allHistoryQuerys = [];
                        angular.forEach(respone.data, function(item) {
                            var propArr = [];
                            if (item.properties && item.properties != '{}') {
                                propArr = item.properties.substring(1, item.properties.length - 1).split(', ');
                            }
                            var sql = '';
                            for (var i = 0; i < propArr.length; ++i) {
                                sql += 'set ' + propArr[i] + ';\n';
                            }
                            sql += item.querySql + ';';
                            var singleQuery = [];
//                            singleQuery.push(item.role);
                            singleQuery.push($filter('dateFormat')(item.addTime, 'yyyy-MM-dd<br/>hh:mm:ss'));
                            singleQuery.push(sql);
                            singleQuery.push(item.queryEngine);
                            singleQuery.push(item.queryStatus);
                            singleQuery.push(item.execTime);
                            if (item.queryStatus == 'FINISHED') {
                            	if (item.csvPath != '' && item.xlsxPath != '') {
                            		singleQuery.push('1' + item.uuid);
                            	} else if (item.csvPath != '' && item.xlsxPath == '') {
                            		singleQuery.push('2' + item.uuid);
                            	} else if (item.csvPath == '' && item.xlsxPath != '') {
                            		singleQuery.push('3' + item.uuid);
                            	} else {
                            		singleQuery.push('4' + item.uuid);
                            	}
                            } else {
                            	singleQuery.push('0' + item.uuid);
                            }
                            $scope.allHistoryQuerys.push(singleQuery);
                        });
                        $scope.renderDataTables($scope.allHistoryQuerys, $scope.columnDefs);
                    },
                    function (reason) {
                        alertError('获取查询历史信息失败！');
                    }
                );
            }

            queryHistoryFunc('');
            $('body').on('keyup', 'input[type="search"]', function(data) {
                if (data.keyCode == 13) {
                	queryHistoryFunc($('input[type="search"]').val());
                }
            });

            function parseStatus(status) {
                switch (status) {
                case QUERY_STATUS.READY:
                    return '<span class="' + QUERY_STATUS_COLOR.READY + '">已提交</span>';
                case QUERY_STATUS.RUNNING:
                    return '<span class="' + QUERY_STATUS_COLOR.RUNNING + '">运行中</span>';
                case QUERY_STATUS.STOPPED:
                    return '<span class="' + QUERY_STATUS_COLOR.STOPPED + '">停止</span>';
                case QUERY_STATUS.FINISHED:
                    return '<span class="' + QUERY_STATUS_COLOR.FINISHED + '">成功</span>';
                case QUERY_STATUS.TIMEOUT:
                    return '<span class="' + QUERY_STATUS_COLOR.TIMEOUT + '">超时</span>';
                case QUERY_STATUS.FAILED:
                    return '<span class="' + QUERY_STATUS_COLOR.FAILED + '">失败</span>';
                default:
                    return '<span>' + status + '</span>';
                }
            }
        } ]);