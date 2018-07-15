/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var uploadCtrl = ['$scope', 'commonService', '$timeout', '$uibModal',
        function ($scope, commonService, $timeout, $uibModal) {

            var FILE_DATA = [];

            $scope.fileList = [];

            $scope.getFiles = function (path) {

                if (!commonService.auth()) return;

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
            };

            if (commonService.auth()) {
                $scope.getFiles("");
            }

            // 目录路径
            $scope.dirs = [{label: 'root', path: ""}];

            // 处理路径
            function dealPath(path) {

                var pathArr = path.split("/");
                pathArr.splice(0, 1); // 去除首个空串
                var temp = [{label: 'root', path: ''}];
                pathArr.forEach(function (item, index) {
                    var curTemp = pathArr.slice(0, index + 1);
                    temp.push({
                        label: item,
                        path: '/' + curTemp.join("/")
                    });
                });
                $scope.dirs = temp;
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
                    var curPath = $scope.dirs[$scope.dirs.length - 1].path;

                    $.ajax({
                        url: '/createDir',
                        type: 'POST',
                        data: {
                            relativePath: curPath,
                            dirName: data.folderName
                        },
                        success: function (resp) {
                            if (resp == 'success') {
                                $timeout(function () {
                                    $scope.fileList.push({
                                        fileName: data.folderName,
                                        dir: true,
                                        relativePath: curPath + "/" + data.folderName
                                    });
                                });
                            } else {
                                commonService.showMessage($scope, 'error', resp);
                            }
                        },
                        error: function (err) {
                            console.log(err)
                        }
                    })
                });

            };

            // 点击文件名
            $scope.fileClick = function (item) {
                if (item.dir) {
                    $scope.getFiles(item.relativePath);
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
                commonService.confirm('删除文件(夹)：' + item.fileName)
                    .result.then(function (resp) {
                    if (resp) {
                        $.ajax({
                            url: '/deleteFile?relativePath=' + item.relativePath,
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

            // $scope.keyDown = function () {
            //     if (event.keyCode == 13) {
            //         $scope.search();
            //     }
            // };

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

                var curPath = $scope.dirs[$scope.dirs.length - 1].path;
                window.open('/uploader?relativePath=' + curPath);
            }
        }

    ];

    var uploadModule = angular.module('upload.config');
    uploadModule.controller('upload.ctrl', uploadCtrl);

});