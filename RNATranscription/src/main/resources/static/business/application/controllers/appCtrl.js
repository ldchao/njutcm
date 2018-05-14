/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var appCtrl = ['$scope', '$state', function ($scope, $state) {

        $scope.appList = [
            {id: 1, name: '应用-1', icon: 'app-icon-1.png', type: '高级软件', brief: '绘制一组或多组数据的GO二级分类注释柱状图'},
            {id: 2, name: '应用-2', icon: 'app-icon-2.png', type: '可视化', brief: '将表格数据绘制成一个热图。'},
            {id: 3, name: '应用-3', icon: 'app-icon-3.png', type: '表格软件', brief: '从源表格包含的所有属性中，筛选出所关注样本的信息。'},
            {id: 4, name: '应用-4', icon: 'app-icon-4.png', type: '统计', brief: '应用超几何检验，找出与整个基因组背景相比，在差异表达基因中显著性富集的Pathway。'},
            {id: 5, name: '应用-5', icon: 'app-icon-5.png', type: '高级软件', brief: '应用超几何检验，找出与整个基因组背景相比，在差异表达基因中显著富集的GO条目。'},
            {id: 6, name: '应用-6', icon: 'app-icon-6.png', type: '高级软件', brief: ' 用来显示数据元素重叠区域。'},
            {id: 7, name: '应用-7', icon: 'app-icon-7.png', type: '高级软件', brief: '将多个变量通过线性变换，筛选出数个比较重要的变量，即主成分分析。'}
        ];

        $scope.getDetail = function (item) {
            $state.go('appDetail', {id: item.id});
        }

    }];

    var appModule = angular.module('application.config');
    appModule.controller('application.ctrl', appCtrl);

});