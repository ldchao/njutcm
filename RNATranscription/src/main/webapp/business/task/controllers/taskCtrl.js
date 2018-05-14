/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var taskCtrl = ['$scope', 'commonService', '$timeout',
        function ($scope, commonService, $timeout) {

            var TASK_DATA = [];
            $scope.taskList = [];

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

            $scope.key = '';
            $scope.search = function () {
                $scope.taskList = TASK_DATA.filter(function (item) {
                    return (item.taskName.indexOf($scope.key) > -1
                    || item.type.indexOf($scope.key) > -1
                    || item.status.indexOf($scope.key) > -1);
                });
            };

            $scope.download = function (item) {
                $.ajax({
                    url: '/download',
                    type: 'GET',
                    data: {
                        taskId: item.id
                    },
                    success: function (resp) {
                        if (resp != 'success') {
                            $timeout(function () {
                                commonService.showMessage($scope, 'error', resp);
                            });
                        }
                    },
                    error: function (err) {
                        console.log(err);
                    }
                });
            };

        }

    ];

    var taskModule = angular.module('task.config');
    taskModule.controller('task.ctrl', taskCtrl);

});