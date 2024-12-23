package cz.geek.fio;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FioClientSettings.class)
public class FioClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FioClient fioClient(FioClientSettings settings, ObjectProvider<RestTemplateBuilder> builder) {
        return new FioClient(settings, builder.getIfAvailable());
    }
}
