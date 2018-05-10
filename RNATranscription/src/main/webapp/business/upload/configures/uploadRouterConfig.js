/**
 * Created by L.H.S on 2017/8/14.
 */

define(['lazy-load/lazyLoad'],

    function (lazyLoadModule) {
        'use strict';

        var configArr = [{
            name: 'upload',
            url: '/upload',
            templateUrl: 'business/upload/views/upload.html',
            controller: 'upload.ctrl',
            scripts: {
                controllers: ['business/upload/controllers/uploadCtrl']
            }
        }];

        var uploadModule = angular.module('upload.config', ['ui.router']);
        uploadModule = lazyLoadModule.makeLazy(uploadModule);
        // stateConfig属性配置路由状态基本信息；urlMatch属性配置异常url对应的url路径
        uploadModule.StateConfig({
            stateConfig: configArr,
            urlMatch: [['/upload/', '/upload'], ['/upload', '/upload']]
        });

        return uploadModule;
    });