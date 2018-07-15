/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var chooseCtrl = ['$scope', '$uibModalInstance', '$timeout', 'commonService',
        function ($scope, $uibModalInstance, $timeout, commonService) {

            if (commonService.auth()) {
                $scope.fileList = [{
                    fileName: '根目录',
                    relativePath: '',
                    isDir: true,
                    children: []
                }];
            }

            $scope.selectedFile = "";

            $scope.getFiles = function (node) {
                if (!node.isDir || (node.children && node.children.length != 0)) return;

                // 获取文件
                $.ajax({
                    url: '/getFile?relativePath=' + node.relativePath,
                    type: 'GET',
                    success: function (resp) {
                        node.children = resp;
                    },
                    error: function (err) {
                        console.log(err);
                    }
                })
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss(false);
            };

            $scope.ok = function () {

                if (!$scope.selectedFile || $scope.selectedFile.isDir) {
                    commonService.showMessage($scope, 'error', '请选择文件');
                    return;
                }

                $uibModalInstance.close({
                    fileName: $scope.selectedFile.fileName,
                    relativePath: $scope.selectedFile.relativePath
                });
            };

        }];

    var chooseModule = angular.module('backend.config');
    chooseModule.controller('chooseCtrl', chooseCtrl);

});