(function () {
    'use strict';

    angular
        .module('securityalarmgatewayApp')
        .factory('User', User);

    User.$inject = ['$resource'];

    function User ($resource) {
        var service = $resource(':prefix/api/users/:login/:field', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'},
            'devices': { method:'GET', isArray: true, params: {'login': '@login', field: 'devices', prefix: 'securityalarm'} },
            'addDevice': { method:'POST', params: {'login': '@login', field: 'devices', prefix: 'securityalarm'} }
        });

        return service;
    }
})();
