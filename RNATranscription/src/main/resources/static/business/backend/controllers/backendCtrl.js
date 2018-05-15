/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var backendCtrl = ['$scope', '$state', 'commonService',
        function ($scope, $state, commonService) {

            $scope.userModel = {
                username: '',
                password: ''
            };

            $scope.adminLogin = function () {

                if (!$scope.userModel.username) {
                    commonService.showMessage($scope, 'error', '请输入用户名');
                    return;
                }

                if (!$scope.userModel.password) {
                    commonService.showMessage($scope, 'error', '请输入密码');
                    return;
                }

                $.ajax({
                    url: '/loginBackend',
                    type: 'POST',
                    data: $scope.userModel,
                    success: function (resp) {
                        if (resp == 'success') {
                            $state.go('admin');
                            commonService.login($scope.userModel.username);
                        } else {
                            commonService.showMessage($scope, 'error', resp);
                        }
                    },
                    error: function (err) {
                        console.log(err);
                    },
                    beforeSend: function () {
                        commonService.showLoading();
                    },
                    complete: function () {
                        commonService.hideLoading();
                    }
                });
            }

        }];

    var backendModule = angular.module('backend.config');
    backendModule.controller('backend.ctrl', backendCtrl);

});