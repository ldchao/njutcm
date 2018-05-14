/**
 * Created by L.H.S on 2017/8/14.
 */

define([], function () {

    'use strict';

    var topnavCtrl = ['$scope', '$state', '$uibModal', 'commonService',
        function ($scope, $state, $uibModal, commonService) {

            $scope.loginModal = function (type) {

                var loginModal = $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'framework/login/login.html',
                    controller: 'loginCtrl',
                    resolve: {
                        type: function () {
                            return type;
                        }
                    }
                });

                loginModal.result.then(function (data) {
                    if (type == '登录') {
                        $.ajax({
                            url: '/login',
                            type: 'POST',
                            data: data,
                            success: function (resp) {
                                if (resp == 'success') {
                                    commonService.login(data.username);
                                } else {
                                    commonService.showMessage($scope, 'error', resp);
                                }
                            },
                            error: function (err) {
                                console.log(err);
                            }
                        });
                    } else {

                    }
                });
            };

            // 判断Menu的子状态激活时，是否需要隶属于其父状态
            $scope.isActived = function (firstLevelState) {
                if (typeof (firstLevelState) !== 'undefined') {
                    var rootState = (firstLevelState + '').split('.');
                    return $state.includes(rootState[0]);
                }
                return false;
            };

            // 是否为后台管理
            $scope.isBackend = function () {
                return $state.current.name == 'backend' || $state.current.name == 'admin';
            };

            // 是否已登录
            $scope.hasLogin = function () {
                return commonService.auth();
            }

        }];

    return topnavCtrl;
});