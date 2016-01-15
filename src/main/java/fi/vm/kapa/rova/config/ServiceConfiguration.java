package fi.vm.kapa.rova.config;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import fi.vm.kapa.rova.rest.validation.ValidationContainerRequestFilter;
import fi.vm.kapa.rova.virre.resources.PrhResource;

@Configuration
@ApplicationPath("/")
public class ServiceConfiguration extends ResourceConfig {

    @Value("${api_key}")
    String apiKey;

    @Value("${api_path_prefix}")
    String apiPathPrefix;

    @Value("${request_alive_seconds}")
    Integer requestAliveSeconds;

    @Value("${ssl_keystoretype}")
    String sslKeyStoreType;

    @Value("${ssl_keystore}")
    String sslKeyStore;

    @Value("${ssl_keystorepassword}")
    String sslKeyStorePassword;

    @Value("${ssl_truststoretype}")
    String sslTrustStoreType;

    @Value("${ssl_truststore}")
    String sslTrustStore;

    @Value("${ssl_truststorepassword}")
    String sslTrustStorePassword;

    public ServiceConfiguration() {
        register(PrhResource.class);
    }

    @PostConstruct
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
       // register(new ValidationContainerRequestFilter(apiKey, requestAliveSeconds, apiPathPrefix));
        System.setProperty("javax.net.ssl.keyStoreType", sslKeyStoreType);
        System.setProperty("javax.net.ssl.keyStore", sslKeyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", sslKeyStorePassword);
        System.setProperty("javax.net.ssl.trustStoreType", sslTrustStoreType);
        System.setProperty("javax.net.ssl.trustStore", sslTrustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", sslTrustStorePassword);
    }
}
