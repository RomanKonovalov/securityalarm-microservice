(function() {
    'use strict';

    angular
        .module(('securityalarmgatewayApp'))
        .controller('MyAlarmsController', MyAlarmsController);

    MyAlarmsController.$inject = ['$scope', 'Alarm','User', 'Principal', 'TrackingTypes', 'NotificationTypes'];

    function MyAlarmsController ($scope, Alarm, User, Principal, TrackingTypes, NotificationTypes) {

        var loadAll = function () {
            Principal.identity().then(function(account) {
                $scope.devices = User.devices({'login': account.login});
            });
        };

        loadAll();

        $scope.trackingTypes = TrackingTypes;

        $scope.notificationTypes = NotificationTypes;

        $scope.activateAlarm = function (device) {
            device.alarm.device = {'id': device.id, 'login': device.name};
            Alarm.save(device.alarm, function () {
                loadAll();
            });
        };

        $scope.deactivateAlarm = function (device) {
            Alarm.delete({'id': device.alarm.id}, function () {
                loadAll();
            });
        };

        $scope.updateAlarm = function (alarm) {
            if (!alarm.id) {
                return;
            }
            Alarm.update(alarm, function () {
                loadAll();
            });
        };

    }
})();
