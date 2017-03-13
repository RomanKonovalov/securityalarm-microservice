(function() {
    'use strict';

    angular
        .module('securityalarmgatewayApp')
        .controller('DeviceManagementDetailController', DeviceManagementDetailController);

    DeviceManagementDetailController.$inject = ['$stateParams', 'Device'];

    function DeviceManagementDetailController ($stateParams, Device) {
        var vm = this;

        vm.load = load;
        vm.device = {};

        vm.load($stateParams.login);

        function load (login) {
            Device.get({login: login}, function(result) {
                vm.device = result;
            });
        }
    }
})();
