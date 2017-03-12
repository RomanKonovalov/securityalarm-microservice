(function() {
    'use strict';

    angular
        .module(('securityalarmgatewayApp'))
        .controller('MyDevicesController', MyDevicesController);

    MyDevicesController.$inject = ['$scope', 'Device', 'Principal', 'User'];

    function MyDevicesController ($scope, Device, Principal, User) {

        var loadAll = function () {
            Principal.identity().then(function(account) {
                $scope.devices = User.devices({'login': account.login});
            });
        };

        loadAll();

        $scope.saveDevice = function (device) {
            Device.update(device, function () {
                loadAll();
            });
        };

        $scope.configDevice = function (device) {
            Device.config({'login': device.name});
        };

    }
})();
