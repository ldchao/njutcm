/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var chooseCtrl = ['$scope', '$uibModalInstance',
        function ($scope, $uibModalInstance) {

            $.ajax({
                url: '/getFileByUser',
                type: 'GET',
                success: function (resp) {
                    $scope.fileList = resp;
                },
                error: function (err) {
                    console.log(err);
                }
            });

            $scope.chosen = '';

            $scope.cancel = function () {
                $uibModalInstance.dismiss(false);
            };

            $scope.ok = function () {

                if (!$scope.chosen) {
                    commonService.showMessage($scope, 'error', '请选择文件');
                    return;
                }

                $uibModalInstance.close($scope.fileList[$scope.chosen]);
            };

        }];

    var chooseModule = angular.module('backend.config');
    chooseModule.controller('chooseCtrl', chooseCtrl);

});