'use strict';

var createTableController = angular.module('hiveWebApp.create.table.controllers', ["angularFileUpload"]);

createTableController.controller('createTableCtrl', ['$scope', '$modal', 'TmpTable', 'FileUploader', 'Role',
    function ($scope, $modal, TmpTable, FileUploader, Role) {

        $scope.columnSeparator = ['\\001', '\\t', ','];
        $scope.columnType = ['TINYINT', 'SMALLINT', 'INT', 'BIGINT', 'BOOLEAN', 'FLOAT', 'DOUBLE', 'STRING', 'BINARY', 'TIMESTAMP'];
        $scope.fileFormats = ['TextFile', 'SequenceFile', 'RCFile', 'InputFormat', 'ORC'];
        $scope.isShowFormat = false;
        $scope.isDisabledCreateButton = true;

        Role.getAll().success(function (data) {
            $scope.allQueryRoles = data;
            if (data.length === 0) {
                alertNoRole();
            }
        });
        Role.getDefault().success(function (data) {
            $scope.selectedRole = data;
        });

        // 如果用户选择的 InputFormat， 需要输入input/output format class
        $scope.$watch("createTableInfo.fileFormat", function () {
            if ($scope.createTableInfo.fileFormat != undefined && $scope.createTableInfo.fileFormat == $scope.fileFormats[3]) {
                $scope.isShowFormat = true;
            } else {
                $scope.isShowFormat = false;
            }
        });

        $scope.createTableInfo = {
            databaseName: '',
            tableName: '',
            tableComment: '',
            colSeparator: $scope.columnSeparator[1],
            isExternal: false,
            tableStoragePath: '',
            fileFormat: $scope.fileFormats[0],
            inputFormat: '',
            outputFormat: '',
            columnList: [
                {columnName: '', columnType: $scope.columnType[0], columnComment: ''}
            ],
//        columnName: [''],
//        columnType: [$scope.columnType[0]],
//        columnComment: [''],
            createTableSql: ''
        };


        $scope.loadTableInfo = {
            roleName: '',
            databaseName: '',
            tableName: '',
            isOverWrite: true,
            partition: '',
            fileList: []
        };

        $scope.addTableColumn = function () {
            $scope.createTableInfo.columnList.push(
                {columnName: '', columnType: $scope.columnType[0], columnComment: ''}
            );
        };
        $scope.deleteTableColumn = function (index) {
            $scope.createTableInfo.columnList.splice(index, 1);
        };

        // 根据输入信息构建 创建临时表的sql语句
        $scope.buildCreateTableSQL = function () {
            if ($scope.createTableInfo.databaseName == '') {
                alertWarning('必填项缺失', '请填写数据库名...');
                return;
            }
            if ($scope.createTableInfo.tableName == '') {
                alertWarning('必填项缺失', '请填写表名...');
                return;
            }
            if ($scope.createTableInfo.fileFormat == $scope.fileFormats[3]
                && ($scope.createTableInfo.inputFormat == '' || $scope.createTableInfo.outputFormat == '')) {
                alertWarning('必填项缺失', '请填写文件输入或者输出格式...');
                return;
            }
            angular.forEach($scope.createTableInfo.columnList, function (col) {
                if (col.columnName == '') {
                    alertWarning('必填项缺失', '请填写列名...');
                    return;
                }
                if (col.columnComment == '') {
                    alertWarning('必填项缺失', '请填写列注释...');
                    return;
                }
            })
            TmpTable.buildCreateTableSQL($scope.createTableInfo).then(
                function (response) {
                    $scope.createTableInfo.createTableSql = response.data.result;
                    if (!response.data.success) {
                        alertError('构建 建表语句', response.data.messages);
                    } else {
                        $scope.isDisabledCreateButton = false;
                    }

                },
                function (reason) {
                    alertError('构建 建表语句', '获取建表语句发生异常...');
                }
            );
        };

        // 执行临时表创建语句
        $scope.createTable = function () {
            TmpTable.createTable(
                {
                    hql: $scope.createTableInfo.createTableSql,
                    roleName: $scope.selectedRole,
                    database: $scope.createTableInfo.databaseName
                }
            ).then(
                function (response) {
                    var result = response.data;
                    if (result.success) {
                        alertSuccess('创建 临时表', '建表成功...');
                    } else {
                        alertError('创建 临时表', result.messages);
                    }
                },
                function (reason) {
                    alertError('构建 建表语句', '创建临时表发生异常...');
                }
            )
        };

        //  临时表上传数据
        $scope.submitLoadData = function () {
            if ($scope.loadTableInfo.databaseName == '') {
                alertWarning('上传表数据', '数据库名必须填写');
                return;
            }
            if ($scope.loadTableInfo.tableName == '') {
                alertWarning('上传表数据', '表名必须填写');
                return;
            }
            for (var i = 0; i < $scope.uploader.queue.length; i++) {
//                if ($scope.loadTableInfo.fileList.indexOf($scope.uploader.queue[i].file.name) == -1) {
//                    $scope.loadTableInfo.fileList.push($scope.uploader.queue[i].file.name);
//                } else {
//                    alertWarning('上传文件', '上传的文件中不允许包含同名文件【' + $scope.uploader.queue[i].file.name + '】，请修改！')
//                    break;
//                }
                $scope.loadTableInfo.fileList.push($scope.uploader.queue[i].file.name);
            }
            ;
            if ($scope.loadTableInfo.fileList.length == 0) {
                alertWarning('上传表数据', '请选择文件并上传');
                return;
            }
            if ($scope.selectedRole == '' || $scope.selectedRole == undefined) {
                alertWarning('角色', '请选择执行角色');
                return;
            }
            $scope.loadTableInfo.roleName = $scope.selectedRole;
            TmpTable.loadDataIntoTmpTable($scope.loadTableInfo).then(
                function (response) {
                    var result = response.data;
                    // 临时表数据文件加载成功后， 从队列中清除
                    $scope.uploader.clearQueue();
                    $scope.loadTableInfo.fileList = [];
                    if (result.success) {
                        alertSuccess('上传临时表数据', '临时表数据上传成功...');
                    } else {
                        alertError('上传临时表数据', result.messages);
                    }
                },
                function (reason) {
                    alertError('上传临时表数据', '上传临时表数据时 发生异常...');
                }
            )
        };

        /************************ file upload ***********************/
        $scope.uploader = new FileUploader({
            url: '/rest/tmpTable/upload'
        });
        $scope.uploader.onSuccessItem = function (fileItem, response, status, headers) {
            if (!response.success) {
                alertError('上传文件失败', response.messages);
                fileItem.progress = 0;
                fileItem.isSuccess = false;
                fileItem.isError = true;
            }
            console.info('onSuccessItem', fileItem, response, status, headers);
        };
        $scope.uploader.onErrorItem = function (fileItem, response, status, headers) {
            alertError('上传文件', '上传文件发生异常...');
            console.info('onErrorItem', fileItem, response, status, headers);
        };
        $scope.uploader.onAfterAddingFile = function (fileItem) {
            var flag = 0;
            for (var i = 0; i < $scope.uploader.queue.length; i++) {
                if ($scope.uploader.queue[i].file.name == fileItem.file.name) {
                    flag += 1;
                    if (2 == flag) {
                        alertError("上传文件", "不允许有同名文件，请修改！");
                        break;
                    }
                }
            }
        };


        // 没有任何角色的提示信息
        function alertNoRole() {
            var modalInstance = $modal.open({
                templateUrl: 'pages/has-no-role.html',
                controller: function ($scope, $modalInstance) {
                    $scope.ok = function () {
                        $modalInstance.close();
                    };
                }
            });
            modalInstance.result.then(function () {
                $scope.window.location.href = 'http://auth.data.dp';
            });
        }
    }]);