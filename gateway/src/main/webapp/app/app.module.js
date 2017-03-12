(function() {
    'use strict';

    angular
        .module('securityalarmgatewayApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar',
            'uiGmapgoogle-maps',
            'angular-jquery-locationpicker',
            'checklist-model',
            'uuid',
            'betsol.intlTelInput'
        ])
        .config(config)
        .run(run)
        ;

    run.$inject = ['stateHandler', 'translationHandler'];

    config.$inject = ['uiGmapGoogleMapApiProvider', 'intlTelInputOptions'];

    function run(stateHandler, translationHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
    }

    function config(uiGmapGoogleMapApiProvider, intlTelInputOptions) {
        uiGmapGoogleMapApiProvider.configure({
            key: 'AIzaSyCIWfhgaHMCsGASFQ6ZNhKA4MbjPcJLaz8',
            v: '3.20', //defaults to latest 3.X anyhow
            libraries: 'weather,geometry,visualization'
        });

        angular.extend(intlTelInputOptions, {
            geoIpLookup: function(callback) {
                $.get("http://ipinfo.io", function() {}, "jsonp").always(function(resp) {
                    var countryCode = (resp && resp.country) ? resp.country : "";
                    callback(countryCode);
                });
            },
            initialCountry: 'auto'
        });
    }

})();
