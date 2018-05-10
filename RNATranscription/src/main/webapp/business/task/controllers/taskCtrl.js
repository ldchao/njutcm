/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var taskCtrl = ['$scope',
        function ($scope) {

            var TASK_DATA = [
                {id: 'task-1', app: 'app-1', time: '2018-05-10 19:53', result: 'success'},
                {id: 'task-2', app: 'app-2', time: '2018-05-10 20:53', result: 'fail'},
                {id: 'task-3', app: 'app-3', time: '2018-05-10 21:53', result: 'process'}
            ];
            $scope.taskList = angular.copy(TASK_DATA);

            $scope.key = '';
            $scope.search = function () {
                $scope.taskList = TASK_DATA.filter(function (item) {
                    return (item.id.indexOf($scope.key) > -1
                    || item.app.indexOf($scope.key) > -1
                    || item.result.indexOf($scope.key) > -1);
                });
            };

            $scope.download = function () {

            };

        }

    ];

    var taskModule = angular.module('task.config');
    taskModule.controller('task.ctrl', taskCtrl);

});