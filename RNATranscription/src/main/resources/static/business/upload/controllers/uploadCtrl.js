/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var uploadCtrl = ['$scope', 'commonService', '$timeout',
        function ($scope, commonService, $timeout) {

            var FILE_DATA = [];
            $scope.fileList = [];

            function getFiles() {
                $.ajax({
                    url: '/getFileByUser',
                    type: 'GET',
                    success: function (resp) {
                        resp.forEach(function (item) {
                            item.uploadAt_f = new Date(item.uploadAt).Format("yyyy-MM-dd hh:mm:ss");
                        });
                        FILE_DATA = resp;
                        $timeout(function () {
                            $scope.fileList = resp;
                        });
                    },
                    error: function (err) {
                        console.log(err)
                    }
                });
            }

            if (commonService.auth()) {
                getFiles();
            }

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
            }
        }

    ];

    var uploadModule = angular.module('upload.config');
    uploadModule.controller('upload.ctrl', uploadCtrl);

});