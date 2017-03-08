(function () {
    'use strict';

    angular
        .module('securityalarmgatewayApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
