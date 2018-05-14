/**
 * Created by L.H.S on 2017/8/14.
 */

/**
 * 主框架module，该模块依赖于angularjs ng module和tiny wcc module
 * 所有的service router都在这里进行统一的配置
 */

define(['ui-router/angular-ui-router',
        'framework/commonService',
        'framework/topnav/topnavCtrl',
        'framework/leftnav/leftnavCtrl',
        'framework/login/loginCtrl',
        'framework/confirm/confirmCtrl',
        'business/application/configures/appRouterConfig',
        'business/upload/configures/uploadRouterConfig',
        'business/task/configures/taskRouterConfig',
        'business/backend/configures/backendRouterConfig'
    ],
    function (router, commonService, topnavCtrl, leftnavCtrl, loginCtrl, confirmCtrl,
              appRouterConfig, uploadRouterConfig, taskRouterConfig, backendRouterConfig) {
        'use strict';

        // 注入框架的配置文件（新增业务模块在此处添加注册）
        var dependency = [
            'ngAnimate',
            'ngSanitize',
            'ui.bootstrap',
            'ui.router',
            appRouterConfig.name,
            uploadRouterConfig.name,
            taskRouterConfig.name,
            backendRouterConfig.name
        ];

        var framework = angular.module('framework', dependency);

        // 公共服务
        framework.service('commonService', commonService);

        // 导航栏
        framework.controller('topnavCtrl', topnavCtrl);
        framework.controller('leftnavCtrl', leftnavCtrl);
        framework.controller('loginCtrl', loginCtrl);

        // 确认框
        framework.controller('confirmCtrl', confirmCtrl);

        // 初始跳转至首页模块
        framework.config(['$urlRouterProvider', function ($urlRouterProvider) {
            $urlRouterProvider.otherwise('/application');
        }]);

        return framework;
    });
