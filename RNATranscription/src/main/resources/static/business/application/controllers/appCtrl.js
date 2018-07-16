/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var appCtrl = ['$scope', '$state', function ($scope, $state) {

        $scope.appList = [];

        function getApps() {
            var name = ['1-fastQC', '2-filter', '3-cufflinks', '4-count', '5-DESeq2', '6-edgeR', '7-GO', '8-KEGG',
                '9-PCA', '10-3DPCA', '11-pheatmap', '12-Volcano', '13-venn', '14-IDconvert', '15-pie', '16-matrix'];

            var temp = [];
            name.forEach(function (item, index) {
                temp.push({
                    id: index,
                    name: item,
                    icon: 'app-icon-' + (index % 7 + 1) + '.png',
                    type: '类型',
                    brief: '这里是简介'
                });
            });
            $scope.appList = temp;
        }

        getApps();

        $scope.getDetail = function (item) {
            $state.go('appDetail', {id: item.id});
        }

    }];

    var appModule = angular.module('application.config');
    appModule.controller('application.ctrl', appCtrl);

});