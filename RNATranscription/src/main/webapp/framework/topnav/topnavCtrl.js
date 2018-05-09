/**
 * Created by L.H.S on 2017/8/14.
 */

define([], function () {

    'use strict';

    var topnavCtrl = ['$scope', '$state', function ($scope, $state) {
        $scope.menus = {
            url: 'framework/topnav/topnav.tpl.html'
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