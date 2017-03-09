package com.romif.securityalarm.config;

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

        private final Authentication authentication = new Authentication();

        private final Sms sms = new Sms();

        public Authentication getAuthentication() {
            return authentication;
        }

        public Sms getSms() {
            return sms;
        }

        public static class Authentication {

            private final Oauth oauth = new Oauth();

            public Oauth getOauth() {
                return oauth;
            }

            public static class Oauth {

                private String clientid;

                private String secret;

                private int tokenValidityInSeconds = 1800;

                public String getClientid() {
                    return clientid;
                }

                public void setClientid(String clientid) {
                    this.clientid = clientid;
                }

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }

                public int getTokenValidityInSeconds() {
                    return tokenValidityInSeconds;
                }

                public void setTokenValidityInSeconds(int tokenValidityInSeconds) {
                    this.tokenValidityInSeconds = tokenValidityInSeconds;
                }
            }

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

        private final Cache cache = new Cache();

        private String host;

        public Cache getCache() {
            return cache;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public static class Cache {

            private int timeToLiveInDays = 1461;

            public int getTimeToLiveInDays() {
                return timeToLiveInDays;
            }

            public void setTimeToLiveInDays(int timeToLiveInDays) {
                this.timeToLiveInDays = timeToLiveInDays;
            }
        }
    }

}
