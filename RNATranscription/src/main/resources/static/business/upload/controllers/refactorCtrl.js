/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var refactorCtrl = ['$scope', '$uibModalInstance', '$timeout', 'commonService',
        function ($scope, $uibModalInstance, $timeout, commonService) {

            if (commonService.auth()) {
                $scope.fileList = [{
                    fileName: 'root',
                    relativePath: '',
                    dir: true,
                    children: []
                }];
            }

            $scope.selectedFile = "";

            $scope.getFiles = function (node) {
                if (!node.dir || (node.children && node.children.length != 0)) return;

                // 获取文件
                $.ajax({
                    url: '/getFile?relativePath=' + node.relativePath,
                    type: 'GET',
                    success: function (resp) {

                        // 去除文件
                        resp.forEach(function (item, index) {
                            if (!item.dir) {
                                resp.splice(index, 1);
                            }
                        });

                        $timeout(function () {
                            node.children = resp;
                        });
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

                if (!$scope.selectedFile) {
                    commonService.showMessage($scope, 'error', '请选择文件夹');
                    return;
                }

                $uibModalInstance.close($scope.selectedFile.relativePath);
            };

        }];

    var refactorModule = angular.module('upload.config');
    refactorModule.controller('refactorCtrl', refactorCtrl);

});