package fi.vm.kapa.rova.config;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import fi.vm.kapa.rova.rest.validation.ValidationContainerRequestFilter;
import fi.vm.kapa.rova.virre.resources.VIRREResource;

@Configuration
@ApplicationPath("/")
public class ServiceConfiguration extends ResourceConfig {

    @Value("${api_key}")
    String apiKey;

    @Value("${api_path_prefix}")
    String apiPathPrefix;

    @Value("${request_alive_seconds}")
    Integer requestAliveSeconds;

    public ServiceConfiguration() {
        register(VIRREResource.class);
    }

    @PostConstruct
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
       // register(new ValidationContainerRequestFilter(apiKey, requestAliveSeconds, apiPathPrefix));
    }
}
