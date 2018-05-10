/**
 * Created by L.H.S on 2017/8/14.
 */

define([], function () {

    'use strict';

    var topnavCtrl = ['$scope', '$state', '$uibModal', function ($scope, $state, $uibModal) {

        $scope.menus = {
            url: 'framework/topnav/topnav.html'
        };

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
    }];

    return topnavCtrl;
});