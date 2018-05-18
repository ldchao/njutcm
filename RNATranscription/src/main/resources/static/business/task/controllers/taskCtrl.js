/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var taskCtrl = ['$scope', 'commonService', '$timeout', '$interval',
        function ($scope, commonService, $timeout, $interval) {

            var TASK_DATA = [];
            $scope.taskList = [];

            $scope.getTasks = function () {
                $.ajax({
                    url: '/getAllTaskByUser',
                    type: 'GET',
                    success: function (resp) {
                        resp.forEach(function (item) {
                            item.startAt_f = new Date(item.startAt).Format("yyyy-MM-dd hh:mm:ss");
                            if (item.endAt) {
                                item.endAt_f = new Date(item.endAt).Format("yyyy-MM-dd hh:mm:ss");
                            }
                        });
                        TASK_DATA = resp;
                        $timeout(function () {
                            $scope.taskList = resp;
                        });
                    },
                    error: function (err) {
                        console.log(err)
                    }
                });
            };

            if (commonService.auth()) {
                $scope.getTasks();
            }

            $scope.key = '';
            $scope.search = function () {
                $scope.taskList = TASK_DATA.filter(function (item) {
                    return (item.taskName.indexOf($scope.key) > -1
                    || item.type.indexOf($scope.key) > -1
                    || item.status.indexOf($scope.key) > -1);
                });
            };

            /** 定时刷新 */
            if (commonService.auth()) {
                var refresh = $interval($scope.getTasks, 6 * 1000);
            }
            $scope.$on('$destroy', function () {
                $interval.cancel(refresh);
            });
            $(window).blur(function () { // 窗口失去焦点
                $interval.cancel(refresh);
            });
            $(window).focus(function () { // 窗口获得焦点
                if (commonService.auth()) {
                    refresh = $interval($scope.getTasks, 6 * 1000);
                }
            });

            $scope.refreshBtn = function () {
                if (!commonService.auth()) {
                    commonService.showMessage($scope, 'error', '请先登录');
                    return;
                }

                $scope.getTasks();
            }
        }

    ];

    var taskModule = angular.module('task.config');
    taskModule.controller('task.ctrl', taskCtrl);

});