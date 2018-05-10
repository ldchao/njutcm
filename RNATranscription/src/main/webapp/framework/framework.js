/**
 * Created by L.H.S on 2017/8/14.
 */

/**
 * 主框架module，该模块依赖于angularjs ng module和tiny wcc module
 * 所有的service router都在这里进行统一的配置
 */

define(['ui-router/angular-ui-router',
        'framework/topnav/topnavCtrl',
        'framework/leftnav/leftnavCtrl',
        'business/application/configures/appRouterConfig',
        'business/upload/configures/uploadRouterConfig',
        'business/task/configures/taskRouterConfig'
    ],
    function (router, topnavCtrl, leftnavCtrl,
              appRouterConfig, uploadRouterConfig, taskRouterConfig) {
        'use strict';

        // 注入框架的配置文件（新增业务模块在此处添加注册）
        var dependency = [
            'ngAnimate',
            'ngSanitize',
            'ui.bootstrap',
            'ui.router',
            appRouterConfig.name,
            uploadRouterConfig.name,
            taskRouterConfig.name
        ];

        var framework = angular.module('framework', dependency);

        framework.controller('topnavCtrl', topnavCtrl); // 顶部菜单控制器
        framework.controller('leftnavCtrl', leftnavCtrl); // 顶部菜单控制器

        // 初始跳转至首页模块
        framework.config(['$urlRouterProvider', function ($urlRouterProvider) {
            $urlRouterProvider.otherwise('/application');
        }]);

        return framework;
    });
