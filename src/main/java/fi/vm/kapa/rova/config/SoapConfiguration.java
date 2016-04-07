package fi.vm.kapa.rova.config;

import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfoPortType;
import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfoPortType;
import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresentPortType;
import org.apache.cxf.clustering.LoadDistributorFeature;
import org.apache.cxf.clustering.RandomStrategy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configures soap clients
 * Created by Juha Korkalainen on 5.4.2016.
 */
@Configuration
public class SoapConfiguration {

    @Value("${xroad_endpoint}")
    String xroadEndpoint;

    @Bean
    XRoadPersonActiveRoleInfoPortType personActiveRolesClient() {
        return (XRoadPersonActiveRoleInfoPortType) jaxWsProxyFactoryBean(XRoadPersonActiveRoleInfoPortType.class).create();
    }

    @Bean
    XRoadCompanyRepresentInfoPortType companyRepresentClient() {
        return (XRoadCompanyRepresentInfoPortType) jaxWsProxyFactoryBean(XRoadCompanyRepresentInfoPortType.class).create();
    }

    @Bean
    XRoadRightToRepresentPortType rightToRepresentClient() {
        return (XRoadRightToRepresentPortType) jaxWsProxyFactoryBean(XRoadRightToRepresentPortType.class).create();
    }

    private JaxWsProxyFactoryBean jaxWsProxyFactoryBean(Class target) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        // load distribution
        LoadDistributorFeature loadDistributorFeature = new LoadDistributorFeature();
        RandomStrategy ldStrategy = new RandomStrategy();
        ldStrategy.setAlternateAddresses(getEndpoints());
        loadDistributorFeature.setStrategy(ldStrategy);
        factory.getFeatures().add(loadDistributorFeature);
        factory.setServiceClass(target);
        return factory;
    }

    private List<String> getEndpoints() {
        String[] endpoints = xroadEndpoint.split(",");
        List<String> list = Arrays.asList(endpoints);
        return list;
    }

}
