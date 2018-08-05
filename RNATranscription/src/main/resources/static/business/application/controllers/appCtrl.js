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

            var breifs = [
                '快速多线程的对测序数据进行质量评估(Quality Control)',
                '数据质控----去除接头与低质量碱基',
                '使用hisat2、cufflinks、stringtie软件进行基因表达量的计算和差异表达基因的寻找',
                'HTSeq软件，根据SAM/BAM比对结果文件和基因注释GTF文件得到基因水平的count',
                'DEseq2软件：对基于count的表达矩阵进行分析',
                'edgeR软件：对基于count的表达矩阵进行分析',
                'GO注释：应用超几何检验，找出与整个基因组背景相比，在差异表达基因中显著富集的GO条目',
                'KEGG注释：应用超几何检验，找出与整个基因组背景相比，在差异表达基因中显著性富集的Pathway',
                'PCA分析：将多个变量通过线性变换，筛选出数个比较重要的变量，即主成分分析',
                '3DPCA：将多个变量通过线性变换，筛选出数个比较重要的变量,用3D形式展示出来',
                '热图：将表格数据绘制成一个热图',
                '火山图：用来显示两组样品间的差异显著性',
                '韦恩图：用来显示数据元素重叠区域',
                'IDconvert：基因格式转换',
                '将表格数据绘制成一个饼图',
                '利用count表格构建基因表达矩阵'
            ];

            var temp = [];
            name.forEach(function (item, index) {
                temp.push({
                    id: index,
                    name: item,
                    icon: 'app-icon-' + index + '.jpg',
                    // type: '类型',
                    brief: breifs[index]
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