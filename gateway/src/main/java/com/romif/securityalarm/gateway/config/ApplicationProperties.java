package com.romif.securityalarm.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to JHipster.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Security security = new Security();

    private final Http http = new Http();

    public Security getSecurity() {
        return security;
    }

    public Http getHttp() {
        return http;
    }

    public static class Security {

        private final Sms sms = new Sms();

        public Sms getSms() {
            return sms;
        }

        public static class Sms {
            private String url;
            private String apikey;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getApikey() {
                return apikey;
            }

            public void setApikey(String apikey) {
                this.apikey = apikey;
            }
        }
    }

    public static class Http {

        private String host;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

    }

}
