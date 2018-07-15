/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var inputModalCtrl = ['$scope', '$uibModalInstance', '$timeout', 'commonService', 'formModel',
        function ($scope, $uibModalInstance, $timeout, commonService, formModel) {

            $scope.formModel = formModel;

            $scope.dataModel = {};

            $scope.cancel = function () {
                $uibModalInstance.dismiss(false);
            };

            $scope.ok = function () {

                if (!$scope.dataModel[formModel.key]) {
                    commonService.showMessage($scope, 'error', '请输入' + formModel.label);
                    return;
                }

                $uibModalInstance.close($scope.dataModel);
            };

        }];

    var inputModalModule = angular.module('upload.config');
    inputModalModule.controller('inputModalCtrl', inputModalCtrl);

});