/**
 * Created by L.H.S on 2018/5/7.
 */

define([''], function () {
    'use strict';

    var confirmCtrl = ['$scope', '$uibModalInstance', 'content', function ($scope, $uibModalInstance, content) {

        $scope.content = content;

        $scope.cancel = function () {
            $uibModalInstance.close(false);
        };

        $scope.ok = function () {
            $uibModalInstance.close(true);
        };

    }];

    return confirmCtrl;
});