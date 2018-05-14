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
            var refresh = $interval($scope.getTasks, 6 * 1000);
            $scope.$on('$destroy', function () {
                $interval.cancel(refresh);
            });
            $(window).blur(function () { // 窗口失去焦点
                $interval.cancel(refresh);
            });
            $(window).focus(function () { // 窗口获得焦点
                refresh = $interval($scope.getTasks, 6 * 1000);
            });

            // $scope.download = function (item) {
            //     $.ajax({
            //         url: '/download',
            //         type: 'GET',
            //         data: {
            //             taskId: item.id
            //         },
            //         success: function (resp) {
            //             if (resp != 'success') {
            //                 $timeout(function () {
            //                     commonService.showMessage($scope, 'error', resp);
            //                 });
            //             }
            //         },
            //         error: function (err) {
            //             console.log(err);
            //         }
            //     });
            // };

        }

    ];

    var taskModule = angular.module('task.config');
    taskModule.controller('task.ctrl', taskCtrl);

});