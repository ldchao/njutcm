/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var uploadCtrl = ['$scope', 'commonService', '$timeout', '$uibModal',
        function ($scope, commonService, $timeout, $uibModal) {

            var FILE_DATA = [];
            $scope.fileList = [{fileName: 'test', isDir: true}];

            function getFiles(path) {
                $.ajax({
                    url: '/getFile?relativePath=' + path,
                    type: 'GET',
                    success: function (resp) {
                        resp.forEach(function (item) {
                            item.uploadAt_f = new Date(item.lastModifiedTime).Format("yyyy-MM-dd hh:mm:ss");
                        });
                        FILE_DATA = resp;
                        $timeout(function () {
                            $scope.fileList = resp;
                        });
                        dealPath(path);
                    },
                    error: function (err) {
                        console.log(err)
                    }
                });
            }

            // 目录路径
            $scope.dirs = [{label: '根目录', path: ''}];

            // 处理路径
            function dealPath(path) {
                var pathArr = path.split("\\");
                var temp = [{label: '根目录', path: ''}];
                pathArr.forEach(function (item, index) {
                    var curTemp = pathArr.splice(0, index + 1);
                    temp.push({
                        label: item,
                        path: curTemp.join("\\")
                    });
                });
                $scope.dirs = temp;
            }

            if (commonService.auth()) {
                getFiles("");
            }

            // 新建文件夹
            $scope.newFolder = function () {
                if (!commonService.auth()) {
                    commonService.showMessage($scope, 'error', '请先登录');
                    return;
                }

                var inputModal = $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'business/upload/views/inputModal.html',
                    controller: 'inputModalCtrl',
                    resolve: {
                        formModel: function () {
                            return {
                                label: '文件夹名称',
                                key: 'folderName'
                            };
                        }
                    }
                });

                inputModal.result.then(function (data) {

                });

            };

            // 点击文件名
            $scope.fileClick = function (item) {
                if (item.isDir) {
                    getFiles(item.relativePath);
                    dealPath(item.relativePath);
                } else {
                    // 文件
                }
            };

            // 重命名
            $scope.renameFile = function (item, index) {

                var inputModal = $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'business/upload/views/inputModal.html',
                    controller: 'inputModalCtrl',
                    resolve: {
                        formModel: function () {
                            return {
                                label: '文件名称',
                                key: 'filename'
                            };
                        }
                    }
                });

                inputModal.result.then(function (data) {
                    $.ajax({
                        url: '/renameFile',
                        type: 'POST',
                        data: {
                            oldPath: item.relativePath,
                            newName: data.filename
                        },
                        success: function (resp) {
                            $scope.fileList[index].fileName = data.filename;
                        },
                        error: function (err) {
                            console.log(err);
                        }
                    })
                });

            };

            // 移动
            $scope.moveFile = function (item, index) {

            };

            // 删除
            $scope.deleteFile = function (item, index) {
                commonService.confirm('删除文件：' + item.fileName)
                    .result.then(function (resp) {
                    if (resp) {
                        $.ajax({
                            url: '/deleteFileById?fileId=' + item.id,
                            type: 'DELETE',
                            success: function (resp) {
                                if (resp == 'success') {
                                    $timeout(function () {
                                        $scope.fileList.splice(index, 1);
                                        // FILE_DATA.splice(index, 1);  // 此处会自动删除，大概是因为都指向resp，没有copy
                                    });
                                } else {
                                    commonService.showMessage($scope, 'error', resp);
                                }
                            },
                            error: function (err) {
                                console.log(err);
                            }
                        });
                    }
                });
            };

            $scope.key = '';
            $scope.search = function () {
                $scope.fileList = FILE_DATA.filter(function (item) {
                    return (item.name.indexOf($scope.key) > -1 || item.type.indexOf($scope.key) > -1);
                });
            };

            $scope.uploader = function () {
                if (!commonService.auth()) {
                    commonService.showMessage($scope, 'error', '请先登录');
                    return;
                }

                window.open('/uploader');
            }
        }

    ];

    var uploadModule = angular.module('upload.config');
    uploadModule.controller('upload.ctrl', uploadCtrl);

});