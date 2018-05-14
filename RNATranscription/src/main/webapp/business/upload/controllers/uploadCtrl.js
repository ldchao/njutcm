/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var uploadCtrl = ['$scope', 'commonService', '$timeout',
        function ($scope, commonService, $timeout) {

            var FILE_DATA = [];
            $scope.fileList = [];

            $.ajax({
                url: '/getFileByUser',
                type: 'GET',
                success: function (resp) {
                    FILE_DATA = resp;
                    $timeout(function () {
                        $scope.fileList = resp;
                    });
                },
                error: function (err) {
                    console.log(err)
                }
            });

            $scope.deleteFile = function (item, index) {
                commonService.confirm('删除文件：' + item.name)
                    .result.then(function (resp) {

                    if (resp) {

                    } else {

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