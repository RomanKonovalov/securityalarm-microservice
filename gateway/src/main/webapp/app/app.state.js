(function() {
    'use strict';

    angular
        .module('securityalarmgatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider', '$locationProvider'];

    function stateConfig($stateProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        $stateProvider.state('app', {
            abstract: true,
            views: {
                'navbar@': {
                    templateUrl: 'app/layouts/navbar/navbar.html',
                    controller: 'NavbarController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
            }
        });
    }
})();
