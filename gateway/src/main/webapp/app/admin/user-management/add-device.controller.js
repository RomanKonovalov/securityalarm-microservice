(function() {
    'use strict';

    angular
        .module(('securityalarmgatewayApp'))
        .controller('AddDeviceController',AddDeviceController);

    AddDeviceController.$inject = ['$uibModalInstance', 'freeDevices', 'user', 'User'];

    function AddDeviceController ($uibModalInstance, freeDevices, user, User) {
        var vm = this;

        vm.devices = freeDevices;

        vm.device;

        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSuccess (result) {
            $uibModalInstance.close();
            //vm.devices = Device.query({login : $stateParams.login});
        }

        vm.add = function (device) {
            User.addDevice({login: user.login}, device, onSuccess);
        };


    }
})();
