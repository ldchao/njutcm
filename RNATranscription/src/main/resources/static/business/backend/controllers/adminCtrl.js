/**
 * Created by L.H.S on 2017/8/14.
 */

define([''], function () {
    'use strict';

    var adminCtrl = ['$scope', '$uibModal', 'commonService', '$timeout',
        function ($scope, $uibModal, commonService, $timeout) {

            $.ajax({
                url: '/getAllUser',
                type: 'GET',
                success: function (resp) {
                    resp.forEach(function (item) {
                        item.lastLoginTime_f = new Date(item.lastLoginTime).Format("yyyy-MM-dd hh:mm:ss");
                    });
                    $timeout(function () {
                        $scope.userList = resp;
                    });
                },
                error: function (err) {
                    console.log(err)
                }
            });

            $scope.addUser = function () {
                var addModal = $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    templateUrl: 'business/backend/views/add.html',
                    controller: 'addCtrl'
                });

                addModal.result.then(function (data) {

                    $.ajax({
                        url: '/addUser',
                        type: 'POST',
                        data: {
                            username: data.username,
                            password: data.password
                        },
                        success: function (resp) {
                            if (resp == 'success') {
                                $scope.userList.push(data)
                            } else {
                                commonService.showMessage($scope, 'error', resp);
                            }
                        },
                        error: function (err) {
                            console.log(err);
                        }
                    })

                });
            };

            $scope.deleteUser = function (item, index) {
                commonService.confirm('删除用户：' + item.username)
                    .result.then(function (resp) {
                    if (resp) {
                        $.ajax({
                            url: 'deleteUser',
                            type: 'DELETE',
                            data: {
                                username: item.username
                            },
                            success: function (resp) {
                                if (resp == 'success') {
                                    $scope.userList.splice(index, 1);
                                } else {
                                    commonService.showMessage($scope, 'error', resp);
                                }
                            },
                            error: function (err) {
                                console.log(err)
                            }
                        })
                    }
                });
            }

        }];

    var adminModule = angular.module('backend.config');
    adminModule.controller('adminCtrl', adminCtrl);

});