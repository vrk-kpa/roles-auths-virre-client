package fi.vm.kapa.rova.config;

import fi.vm.kapa.rova.logging.LogbackConfigurator;
import fi.vm.kapa.rova.logging.MDCFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class Initializer implements WebApplicationInitializer {

    private static final String MDC_FILTER = "mdcFilter";

    @Autowired
    private LogbackConfigurator logConfigurator;

    @Bean
    public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
        return logConfigurator.containerCustomizer();
    }

    @Override
    public void onStartup(ServletContext ctx) throws ServletException {
        ctx.addFilter(MDC_FILTER, MDCFilter.class)
                .addMappingForUrlPatterns(
                        EnumSet.<DispatcherType>of(DispatcherType.REQUEST,
                                DispatcherType.FORWARD), false, "/*");
    }
}
