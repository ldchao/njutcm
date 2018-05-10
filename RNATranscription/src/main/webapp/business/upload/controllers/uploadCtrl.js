/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var uploadCtrl = ['$scope', 'commonService',
        function ($scope, commonService) {

            var FILE_DATA = [
                {name: 'test-1', type: 'java', size: '500KB', date: '2018-05-10'},
                {name: 'test-2', type: 'txt', size: '600KB', date: '2018-05-11'},
                {name: 'test-3', type: 'py', size: '520KB', date: '2018-05-12'},
                {name: 'test-4', type: 'js', size: '700KB', date: '2018-05-13'},
                {name: 'test-5', type: 'php', size: '1500KB', date: '2018-05-14'}
            ];
            $scope.fileList = angular.copy(FILE_DATA);

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