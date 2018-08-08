/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var modPwdCtrl = ['$scope', '$uibModalInstance', 'commonService',
        function ($scope, $uibModalInstance, commonService) {

            $scope.data = {
                oldPassword: '',
                newPassword: '',
                newPassword2: ''
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss(false);
            };

            $scope.ok = function () {

                if (!$scope.data.oldPassword) {
                    commonService.showMessage($scope, 'error', '请输入原密码');
                    return;
                }

                if (!$scope.data.newPassword) {
                    commonService.showMessage($scope, 'error', '请输入新密码');
                    return;
                }

                if ($scope.data.newPassword !== $scope.data.newPassword2) {
                    commonService.showMessage($scope, 'error', '两次密码不一致');
                    return;
                }

                $uibModalInstance.close($scope.data);
            };

        }];

    return modPwdCtrl;
});