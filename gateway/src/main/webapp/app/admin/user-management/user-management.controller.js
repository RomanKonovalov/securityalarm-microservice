(function() {
    'use strict';

    angular
        .module('securityalarmgatewayApp')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User', 'ParseLinks', 'AlertService', '$state', 'pagingParams', 'paginationConstants', 'JhiLanguageService', '$uibModal'];

    function UserManagementController(Principal, User, ParseLinks, AlertService, $state, pagingParams, paginationConstants, JhiLanguageService, $uibModal) {
        var vm = this;

        vm.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        vm.currentAccount = null;
        vm.languages = null;
        vm.loadAll = loadAll;
        vm.setActive = setActive;
        vm.users = [];
        vm.page = 1;
        vm.totalItems = null;
        vm.clear = clear;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;

        vm.loadAll();
        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });

        function setActive (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }

        function loadAll () {
            User.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        }

        function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.page = pagingParams.page;
            vm.users = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

        function clear () {
            vm.user = {
                id: null, login: null, firstName: null, lastName: null, email: null,
                activated: null, langKey: null, createdBy: null, createdDate: null,
                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                resetKey: null, authorities: null
            };
        }

        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        vm.addDevice = function (user) {
            $uibModal.open({
                templateUrl: 'app/admin/user-management/add-device.html',
                controller: 'AddDeviceController',
                controllerAs: 'vm',
                size: 'md',
                resolve: {
                    freeDevices: ['Device', function(Device) {
                        return Device.query({free : true});
                    }],
                    user: function () {
                        return user;
                    }
                }
            }).result.then(function() {
                $state.go('user-management', null, { reload: true });
            }, function() {
                $state.go('user-management');
            });
        }

        vm.showDevices = function (user) {
            $uibModal.open({
                templateUrl: 'app/admin/user-management/device-list.html',
                controller: 'DeviceListController',
                controllerAs: 'vm',
                size: 'md',
                resolve: {
                    devices: ['User', function(User) {
                        return User.devices({login: user.login});
                    }],
                    user: function () {
                        return user;
                    }
                }
            }).result.then(function() {
                $state.go('user-management', null, { reload: true });
            }, function() {
                $state.go('user-management');
            });
        }
    }
})();
