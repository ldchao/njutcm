/**
 * Created by L.H.S on 2017/8/14.
 */

define(['lazy-load/lazyLoad'],

    function (lazyLoadModule) {
        'use strict';

        var configArr = [{
            name: 'task',
            url: '/task',
            templateUrl: 'business/task/views/task.html',
            controller: 'task.ctrl',
            scripts: {
                controllers: ['business/task/controllers/taskCtrl']
            }
        }];

        var taskModule = angular.module('task.config', ['ui.router']);
        taskModule = lazyLoadModule.makeLazy(taskModule);
        // stateConfig属性配置路由状态基本信息；urlMatch属性配置异常url对应的url路径
        taskModule.StateConfig({
            stateConfig: configArr,
            urlMatch: [['/task/', '/task'], ['/task', '/task']]
        });

        return taskModule;
    });