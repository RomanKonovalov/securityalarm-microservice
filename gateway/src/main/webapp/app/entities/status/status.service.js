(function() {
    'use strict';
    angular
        .module(('securityalarmgatewayApp'))
        .factory('Status', Status);

    Status.$inject = ['$resource'];

    function Status ($resource) {
        var resourceUrl =  'securityalarm/' + 'api/statuses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
