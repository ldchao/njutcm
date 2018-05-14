/**
 * Created by L.H.S on 2018/5/10.
 */

define([''], function () {
    'use strict';

    var appDetailCtrl = ['$scope', '$uibModal', 'commonService', '$state', '$timeout',
        function ($scope, $uibModal, commonService, $state, $timeout) {

            $scope.taskModel = {
                taskName: '',
                type: 'all',
                fileId: ''
            };

            $scope.fileName = '';

            $scope.chooseFile = function () {
                var chooseModal = $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'business/application/views/choose.html',
                    controller: 'chooseCtrl'
                });

                chooseModal.result.then(function (data) {
                    $scope.taskModel.fileId = data.id;
                    $scope.fileName = data.fileName;
                });
            };

            $scope.run = function () {
                var data = angular.copy($scope.taskModel);

                if (!data.taskName) {
                    commonService.showMessage($scope, 'error', '请输入任务编号');
                    return;
                }

                if (!data.fileId) {
                    commonService.showMessage($scope, 'error', '请选择文件');
                    return;
                }

                $.ajax({
                    url: '/createTask',
                    type: 'POST',
                    data: data,
                    success: function (resp) {
                        if (resp == 'success') {
                            $state.go("task");
                        } else {
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

        }];

    var appModule = angular.module('application.config');
    appModule.controller('appDetailCtrl', appDetailCtrl);

});