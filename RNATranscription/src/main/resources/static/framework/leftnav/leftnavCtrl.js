/**
 * Created by L.H.S on 2018/5/9.
 */

define([], function () {

    'use strict';

    var leftnavCtrl = ['$scope', '$state', function ($scope, $state) {

        $scope.navList = [
            {state: 'application', label: '我的应用', icon: 'fab fa-windows'},
            {state: 'upload', label: '上传数据', icon: 'fa fa-cloud-upload-alt'},
            {state: 'task', label: '任务列表', icon: 'fa fa-list'}
        ];

        // 判断Menu的子状态激活时，是否需要隶属于其父状态
        $scope.isActived = function (firstLevelState) {
            if (typeof (firstLevelState) !== 'undefined') {
                var rootState = (firstLevelState + '').split('.');
                return $state.includes(rootState[0]);
            }
            return false;
        };
    }];

    return leftnavCtrl;
});