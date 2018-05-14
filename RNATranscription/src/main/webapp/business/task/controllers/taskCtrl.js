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
                    return (item.id.indexOf($scope.key) > -1
                    || item.app.indexOf($scope.key) > -1
                    || item.result.indexOf($scope.key) > -1);
                });
            };

            $scope.download = function (item) {
                $.ajax({
                    url: '/download',
                    type: 'POST',
                    data: {
                        taskId: item.id
                    },
                    success: function (resp) {
                        if (resp != 'success') {
                            commonService.showMessage($scope, 'error', resp);
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