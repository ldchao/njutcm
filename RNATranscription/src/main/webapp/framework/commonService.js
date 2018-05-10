/**
 * Created by L.H.S on 2018/5/10.
 */

define([''], function () {
    'use strict';

    var commonService = function ($timeout, $uibModal) {

        /**
         * 保存登录的用户名
         * */
        var username;

        this.login = function (name) {
            username = name;
        };

        this.auth = function () {
            return username;
        };

        /**
         * 消息提示
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
        };

        /**
         * 确认框
         * 您是否确认{{...}}？
         * */
        this.confirm = function (content) {
            var confirmModal = $uibModal.open({
                animation: true,
                backdrop: 'static',
                templateUrl: 'framework/confirm/confirm.html',
                controller: 'confirmCtrl',
                resolve: {
                    content: function () {
                        return content;
                    }
                }
            });
            return confirmModal;
        }

    };

    return commonService;
});