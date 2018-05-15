/**
 * Created by L.H.S on 2017/8/14.
 */

define([], function () {

    'use strict';

    var topnavCtrl = ['$scope', '$state', '$uibModal', 'commonService', '$timeout',
        function ($scope, $state, $uibModal, commonService, $timeout) {

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
                                    location.reload();
                                } else {
                                    $timeout(function () {
                                        commonService.showMessage($scope, 'error', resp);
                                    });
                                }
                            },
                            error: function (err) {
                                console.log(err);
                            },
                            beforeSend: function () {
                                console.log("before")
                                commonService.showLoading();
                            },
                            complete: function () {
                                console.log("complete")
                                commonService.hideLoading();
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
            };

            // 登出
            var toggle = false;
            $scope.logoutToggle = function () {
                toggle = !toggle;
                if (toggle) {
                    $("#logout").slideDown(300);
                } else {
                    $("#logout").slideUp(100);
                }
            };
            $("#middle").click(function () {
                $("#logout").hide();
                toggle = false;
            });

            $scope.logout = function () {
                $.ajax({
                    url: '/logout',
                    type: 'GET',
                    success: function (resp) {
                        if (resp == 'logout_success') {
                            commonService.logout();
                            location.reload();
                        } else {
                            $timeout(function () {
                                commonService.showMessage($scope, 'error', resp);
                            });
                        }
                    },
                    error: function (err) {
                        console.log(err);
                    }
                })
            };
        }];

    return topnavCtrl;
});