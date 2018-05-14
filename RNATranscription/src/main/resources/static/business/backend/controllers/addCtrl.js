/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var addCtrl = ['$scope', '$uibModalInstance', 'commonService',
        function ($scope, $uibModalInstance, commonService) {

            $scope.data = {
                username: '',
                password: '',
                password2: ''
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss(false);
            };

            $scope.ok = function () {

                if (!$scope.data.username) {
                    commonService.showMessage($scope, 'error', '请输入用户名');
                    return;
                }

                if (!$scope.data.password) {
                    commonService.showMessage($scope, 'error', '请输入密码');
                    return;
                }

                if ($scope.data.password !== $scope.data.password2) {
                    commonService.showMessage($scope, 'error', '两次密码不一致');
                    return;
                }

                $uibModalInstance.close($scope.data);
            };

        }];

    var addModule = angular.module('backend.config');
    addModule.controller('addCtrl', addCtrl);

});