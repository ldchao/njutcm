/**
 * Created by L.H.S on 2018/5/10.
 */

define([''], function () {
    'use strict';

    var commonService = function ($timeout, $uibModal) {

        /**
         * 保存登录的用户名
         * */

        this.login = function (name) {
            sessionStorage.setItem('username', name);
        };

        this.auth = function () {
            return sessionStorage.getItem('username');
        };

        this.logout = function () {
            this.username = '';
            sessionStorage.removeItem('username');
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
        };

        /** 格式化时间 */
        Date.prototype.Format = function (fmt) {
            var o = {
                "M+": this.getMonth() + 1, //Month
                "d+": this.getDate(), //Day
                "h+": this.getHours(), //Hour
                "m+": this.getMinutes(), //Minute
                "s+": this.getSeconds(), //Second
                "q+": Math.floor((this.getMonth() + 3) / 3), //Season
                "S": this.getMilliseconds() //millesecond
            };
            if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt))
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        };

        /** 加载条 */
        var loadInstance;
        this.showLoading = function () {
            loadInstance = $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'framework/loading.html'
            });
        };

        this.hideLoading = function () {
            if (loadInstance)
                loadInstance.close();
        }
    };

    return commonService;
});