/**
 * Created by L.H.S on 2018/5/10.
 */

define([''], function () {
    'use strict';

    var appDetailCtrl = ['$scope', '$uibModal', 'commonService', '$state', '$timeout', 'appService',
        function ($scope, $uibModal, commonService, $state, $timeout, appService) {

            var appId = $state.params.id;
            $scope.formModel = appService.getModel(appId);  // 表单模型

            // 数据类型
            $scope.taskModel = {
                taskName: '',
                type: 'all' // 默认填充
            };

            // 将表单模型填充到数据类型
            $scope.formModel.forEach(function (item) {
                $scope.taskModel[item.key] = '';
            });

            // combox选择
            $scope.comboxSelect = function (key, option) {
                $scope.taskModel[key] = option;
            };

            $scope.fileNameModel = {};

            $scope.chooseFile = function (key) {
                var chooseModal = $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'business/application/views/choose.html',
                    controller: 'chooseCtrl'
                });

                chooseModal.result.then(function (data) {
                    $scope.taskModel[key] = data.relativePath;
                    $scope.fileNameModel[key] = data.fileName;
                });
            };

            /** combox的值为option，需要取其中的value */
            $scope.run = function () {

                if (!commonService.auth()) {
                    commonService.showMessage($scope, 'error', '请先登录');
                    return;
                }

                var data = angular.copy($scope.taskModel);

                if (!data.taskName) {
                    commonService.showMessage($scope, 'error', '请输入任务编号');
                    return;
                }

                /** 非空判断 TODO */

                var ajaxUrl = appService.getUrl(appId)
                $.ajax({
                    url: ajaxUrl,
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

            $scope.uploader = function () {
                if (!commonService.auth()) {
                    commonService.showMessage($scope, 'error', '请先登录');
                    return;
                }

                window.open('/uploader');
            }
        }];

    var appModule = angular.module('application.config');
    appModule.controller('appDetailCtrl', appDetailCtrl);

});