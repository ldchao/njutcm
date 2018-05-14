/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var chooseCtrl = ['$scope', '$uibModalInstance', '$timeout', 'commonService',
        function ($scope, $uibModalInstance, $timeout, commonService) {


            $.ajax({
                url: '/getFileByUser',
                type: 'GET',
                success: function (resp) {
                    $timeout(function () {
                        $scope.fileList = resp;
                    });
                },
                error: function (err) {
                    console.log(err);
                }
            });

            $scope.chosen;

            $scope.cancel = function () {
                $uibModalInstance.dismiss(false);
            };

            $scope.ok = function () {

                if (!$scope.chosen && $scope.chosen !== 0) {
                    commonService.showMessage($scope, 'error', '请选择文件');
                    return;
                }

                $uibModalInstance.close($scope.fileList[$scope.chosen]);
            };

        }];

    var chooseModule = angular.module('backend.config');
    chooseModule.controller('chooseCtrl', chooseCtrl);

});