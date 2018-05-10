/**
 * Created by L.H.S on 2018/5/7.
 */

define([''], function () {
    'use strict';

    var loginCtrl = ['$scope', '$uibModalInstance', 'commonService', 'type', function ($scope, $uibModalInstance, commonService, type) {


        $scope.modalType = type;

        $scope.data = {
            username: '',
            pwd: ''
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss(false);
        };

        $scope.ok = function () {

            if (!$scope.data.username) {
                commonService.showMessage($scope, 'error', '请输入用户名');
                return;
            }

            if (!$scope.data.pwd) {
                commonService.showMessage($scope, 'error', '请输入密码');
                return;
            }

            if (type=='注册' && $scope.data.pwd!==$scope.data.pwd2) {
                commonService.showMessage($scope, 'error', '两次密码不一致');
                return;
            }

            $uibModalInstance.close($scope.data);
        };


    }];

    return loginCtrl;
});