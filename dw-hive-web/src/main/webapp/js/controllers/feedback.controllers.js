'use strict';

/**
 * 意见反馈页面对应的controller
 * */
var feedbackControllers = angular.module('hiveWebApp.feedback.controllers', []);

feedbackControllers.controller('ReplyCtrl', ['$scope', '$http', '$location', '$anchorScroll',
    function ($scope, $http, $location, $anchorScroll) {

        $scope.cursorState = 'pointer';
        $scope.cursorStyle = { 'cursor': $scope.cursorState};

        // 发送按钮的响应
        $scope.submit = function () {
            if (!$scope.reply) {
                alertError('反馈信息', '反馈信息不能为空，请填写信息！');
            } else {
                $scope.cursorState = 'waiting';
                var replyData = {'reply': $scope.reply};

                $http({
                    method: 'POST',
                    url: 'rest/query/reply',
                    data: replyData
                }).then(
                    function (response) {
                        $scope.cursorState = 'pointer';
                        var result = response.data;
                        if (result.success) {
                            alertSuccess('反馈信息', '反馈信息发送成功');
                        } else {
                            alertError('反馈信息', result.messages);
                        }
                    },
                    function (reason) {
                        $scope.cursorState = 'pointer';
                        alertError('反馈信息', '反馈信息发送异常！');
                    }
                );
            }
        };

        $scope.goto = function (id) {
            var old = $location.hash();
            $location.hash(id);
            $anchorScroll();
            $location.hash(old);
        }
    }
]);