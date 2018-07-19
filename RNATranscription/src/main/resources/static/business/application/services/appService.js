/**
 * Created by L.H.S on 2018/6/17.
 */

define([''], function () {
    'use strict';

    var appService = function () {

        var service = this;

        var speciesOpt = [
            {value: '1', label: '人类'},
            {value: '2', label: '小鼠'},
            {value: '3', label: '大鼠'}
        ];

        /** taskName必填: 任务名
         *  若无type字段，则为输入框
         *  type=file，选择文件
         *  type=combox，选项在options
         *  type=radio，单选，选项在options, 一般用combox代替
         *  option：{label，value}
         *  */
        var modelFactory = [
            [{key: 'threadNum', label: '线程数'},
                {key: 'relativePath', type: 'file', label: '质检文件'},
                {
                    key: 'fileType', label: '文件类型', type: 'combox',
                    options: [{value: '1', label: 'fq'}, {value: '2', label: 'fq.gz'}]
                }],  // 1-fastqc
            [{
                key: 'software', label: '软件选择', type: 'combox',
                options: [{value: '1', label: 'trimmomatic'}, {value: '2', label: 'NGSQC'}]
            },
                {
                    key: 'seqType', label: '单/双端', type: 'combox',
                    options: [{value: '1', label: '单端'}, {value: '2', label: '双端'}]
                },
                {key: 'relativePath', label: '过滤文件', type: 'file'},
                {key: 'threadNum', label: '线程数'}
            ], // 2-TODO
            [{key: 'relativePath', type: 'file', label: '测序文件'}, {key: 'sampleNum', label: '样品数'},
                {key: 'threadNum', label: '线程数'},
                {
                    key: 'gene', label: '基因组', type: 'combox', options: [
                    {value: 'human', label: '人类'},
                    {value: 'mouse', label: '小鼠'},
                    {value: 'rat', label: '大鼠'}
                ]
                },
                {key: 'untreated', label: 'untreated组名'},
                {key: 'treated', label: 'treated组名'},
                {
                    key: 'newTranscript', label: '是否选择构建新转录本', type: 'combox',
                    options: [{label: '是', value: 2}, {label: '否', value: 1}]
                }], // 3-cufflinks
            [{
                key: 'seqType', label: '测序方式', type: 'combox',
                options: [{label: '双端测序', value: '2'}, {label: '单端测序', value: '1'}]
            },
                {key: 'threadNum', label: '线程数'},
                {key: 'specie', label: '物种选择', type: 'combox', options: [
                    {value: 'human', label: '人类'},
                    {value: 'mouse', label: '小鼠'},
                    {value: 'rat', label: '大鼠'}
                ]},
                {key: 'relativePath1', label: '左端测序', type: 'file'},
                {key: 'relativePath2', label: '右端测序', type: 'file'},
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
                {key: 'relativePath', label: '基因文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'geneType', label: '基因类型'},
                {key: 'qValue', label: 'qValue设置'}
            ], // 7-GO
            [
                {key: 'relativePath', label: '基因文件', type: 'file'},
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
                {key: 'color1', label: '第一种颜色'},
                {key: 'color2', label: '第二种颜色'},
                {key: 'color3', label: '第三种颜色'},
                {
                    key: 'isCluster', label: '样本是否聚类', type: 'combox',
                    options: [
                        {label: '聚类', value: '1'},
                        {label: '不聚类', value: '2'}]
                }
            ], // 11-pheatmap
            [
                {key: 'heatMap', label: '热图矩阵', type: 'file'},
                {key: 'scatterSize', label: '散点大小'},
                {key: 'xWidth', label: 'X轴宽度'},
                {key: 'yWidth', label: 'Y轴宽度'},
                {key: 'padj', label: 'padj阈值设置'},
                {key: 'color1', label: '第一种颜色'},
                {key: 'color2', label: '第二种颜色'},
                {key: 'color3', label: '第三种颜色'}
            ], // 12-Volcano
            [
                {key: 'relativePath', label: '选择文件', type: 'file'}
            ], // 13-venn
            [
                {key: 'relativePath', label: '基因文件', type: 'file'},
                {key: 'specie', label: '物种选择', type: 'combox', options: speciesOpt},
                {key: 'geneType', label: '基因类型'},
                {key: 'ensembl', label: '目标转化类型'}
            ], // 14-IDconvert
            [
                {key: 'relativePath', label: '选择文件', type: 'file'}
            ], // 15-pie
            [
                {key: 'relativePath', label: '选择文件', type: 'file'},
                {key: 'fileName', label: '文件名称'}
            ]  // 16-matrix
        ];

        service.getModel = function (index) {
            return modelFactory[index];
        };

        var urlFactory = ['/createFastQCTask', '/createTrimmomatic2Task', '/createCufflinksTask', '/createCountTask',
            '/createDESeq2Task', '/createEdgeRTask', '/createGOTask', '/createKEGGTask',
            '/createPCATask', '/create3DPCATask', '/createPheatmapTask', '/createVolcanoTask',
            '/createVennTask', '/createIDconverttask', '/createPieTask', '/createMatrixTask'];

        service.getUrl = function (index) {
            return urlFactory[index];
        }

    };

    var appModule = angular.module('application.config');
    appModule.service('appService', appService);

});