/**
 * Created by L.H.S on 2018/6/17.
 */

define([''], function () {
    'use strict';

    var appService = function () {

        var service = this;

        var speciesOpt = [
            {value: 'human', label: '人类'},
            {value: 'mouse', label: '小鼠'},
            {value: 'rat', label: '大鼠'}
        ];

        /** taskName必填: 任务名
         *  若无type字段，则为输入框
         *  type=file，选择文件
         *  type=combox，选项在options
         *  type=radio，单选，选项在options, 一般用combox代替
         *  option：{label，value}
         *  */
        var modelFactory = [
            [{key: 'treadNum', label: '线程数'}, {key: 'fileId', type: 'file', label: '选择文件'}],  // 1-fastqc
            [], // 2-TODO
            [{key: 'fileId', type: 'file', label: '测序文件'}, {key: 'sampleNum', label: '样品数'},
                {key: 'threadNum', label: '线程数'},
                {key: 'gene', label: '基因组', type: 'combox', options: speciesOpt},
                {key: 'untreated', label: 'untreated组名'},
                {key: 'treated', label: 'treated组名'},
                {
                    key: 'newTranscript', label: '是否选择构建新转录本', type: 'combox',
                    options: [{label: '是', value: 'true'}, {label: '否', value: 'false'}]
                }], // 3-cufflinks
            [{
                key: 'seqType', label: '测序方式', type: 'combox',
                options: [{label: '双端测序', value: '2'}, {label: '单端测序', value: '1'}]
            },
                {key: 'threadNum', label: '线程数'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'left_fileId', label: '左端测序', type: 'file'},
                {key: 'right_fileId', label: '右端测序', type: 'file'},
                {key: 'fileName', label: '生成文件命名'}
            ], // 4-count
            [
                {key: 'matrix', label: '表达矩阵', type: 'file'},
                {key: 'conditionFile', label: '条件文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'qValue', label: 'qValue设置'}
            ], // 5-DESeq2
            [
                {key: 'matrix', label: '表达矩阵', type: 'file'},
                {key: 'conditionFile', label: '条件文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'qValue', label: 'qValue设置'}
            ], // 6-edgeR
            [
                {key: 'fileId', label: '基因文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'geneType', label: '基因类型'},
                {key: 'qValue', label: 'qValue设置'}
            ], // 7-GO
            [
                {key: 'fileId', label: '基因文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'geneType', label: '基因类型'},
                {key: 'qValue', label: 'qValue设置'}
            ], // 8-KEGG
            [
                {key: 'matrix', label: '表达矩阵', type: 'file'},
                {key: 'conditionFile', label: '条件文件', type: 'file'}
            ], // 9-PCA
            [
                {key: 'matrix', label: '表达矩阵', type: 'file'},
                {key: 'conditionFile', label: '条件文件', type: 'file'}
            ], // 10-3DPCA
            [
                {key: 'matrix', label: '表达矩阵', type: 'file'},
                {
                    key: 'standard', label: '标准化', type: 'combox',
                    options: [
                        {label: 'row', value: 'row'},
                        {label: 'column', value: 'column'},
                        {label: 'none', value: 'none'}]
                },
                {
                    key: 'color', label: '颜色', type: 'combox',
                    options: [
                        {label: 'green', value: 'green'},
                        {label: 'white', value: 'white'},
                        {label: 'black', value: 'black'}]
                },
                {
                    key: 'isCluster', label: '样本是否聚类', type: 'combox',
                    options: [
                        {label: '聚类', value: 'cluster'},
                        {label: '不聚类', value: 'no-cluster'}]
                }
            ], // 11-pheatmap
            [
                {key: 'heatMap', label: '热图矩阵', type: 'file'},
                {key: 'scatterSize', label: '散点大小'},
                {key: 'xWidth', label: 'X轴宽度'},
                {key: 'yWidth', label: 'Y轴宽度'},
                {key: 'padj', label: 'padj阈值设置'},
                {
                    key: 'color', label: '颜色', type: 'combox',
                    options: [
                        {label: 'green', value: 'green'},
                        {label: 'white', value: 'white'},
                        {label: 'black', value: 'black'}]
                }
            ], // 12-Volcano
            [], // 13-venn
            [
                {key: 'fileId', label: '基因文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'geneType', label: '基因类型'},
                {key: 'ensembl', label: '目标转化类型'}
            ], // 14-IDconvert
            []  // 15-pie
        ];

        service.getModel = function (index) {
            return modelFactory[index];
        };

    };

    var appModule = angular.module('application.config');
    appModule.service('appService', appService);

});