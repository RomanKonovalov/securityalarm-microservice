(function() {
    'use strict';

    angular
        .module(('securityalarmgatewayApp'))
        .controller('DeviceManagementDialogController',DeviceManagementDialogController);

    DeviceManagementDialogController.$inject = ['$uibModalInstance', 'entity', 'Device'];

    function DeviceManagementDialogController ($uibModalInstance, entity, Device) {
        var vm = this;

        vm.device = entity;

        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSuccess (result) {
            vm.device = result;
        }

        vm.save = function (device) {
            if (device.id) {
                Device.update(device, onSuccess);
            } else {
                Device.save(device, onSuccess);
            }
        };

        vm.login = function (device) {
            Device.login({'login': device.name}, onSuccess);
        };

        vm.logout = function (device) {
            Device.logout({'login': device.name}, onSuccess);
        };

        vm.config = function (device) {
            Device.config({'login': device.name}, onSuccess);
        };

    }
})();
