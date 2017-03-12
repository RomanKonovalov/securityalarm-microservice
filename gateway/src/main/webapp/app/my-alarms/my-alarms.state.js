(function() {
    'use strict';

    angular
        .module(('securityalarmgatewayApp'))
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('myAlarms', {
            parent: 'app',
            url: '/my-alarms',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/my-alarms/my-alarms.html',
                    controller: 'MyAlarmsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                TrackingTypes: ['Util', function (Util) {
                    return Util.trackingTypes();
                }],
                NotificationTypes: ['Util', function (Util) {
                    return Util.notificationTypes();
                }]
            }
        });
    }
})();
