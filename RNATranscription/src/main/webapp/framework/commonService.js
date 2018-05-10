/**
 * Created by L.H.S on 2018/5/10.
 */

define([''], function () {
    'use strict';

    var commonService = function ($timeout) {

        /**
         * type: success, error
         * */
        this.showMessage = function ($scope, type, content) {
            $scope.message = {
                cls: type,
                content: content
            };

            $timeout(function () {
                $scope.message = '';
            }, 3000);
        }

    };

    return commonService;
});