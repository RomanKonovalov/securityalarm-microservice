(function () {
    'use strict';

    angular
        .module('securityalarmgatewayApp')
        .factory('Device', Device);

    Device.$inject = ['$resource'];

    function Device ($resource) {
        var service = $resource('securityalarm/' + 'api/devices/:login/:action', {}, {
            'query': {method: 'GET', isArray: true},
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'},
            'login': { method:'POST', params: {'login': '@login', action: 'login'} },
            'logout': { method:'POST', params: {'login': '@login', action: 'logout'} },
            'config': { method:'POST', params: {'login': '@login', action: 'config'} },
            'statuses': { method:'GET', isArray: true, params: {'login': '@login', action: 'statuses'} }
        });

        return service;
    }
})();
