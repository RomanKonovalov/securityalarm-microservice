(function() {
    'use strict';

    angular
        .module(('securityalarmgatewayApp'))
        .controller('DeviceListController',DeviceListController);

    DeviceListController.$inject = ['$uibModalInstance', 'devices', 'user', 'User'];

    function DeviceListController ($uibModalInstance, devices, user, User) {
        var vm = this;

        vm.devices = devices;

        vm.device;

        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSuccess (result) {
            $uibModalInstance.close();
            //vm.devices = Device.query({login : $stateParams.login});
        }


    }
})();
