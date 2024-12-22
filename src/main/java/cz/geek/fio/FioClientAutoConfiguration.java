package cz.geek.fio;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FioClientSettings.class)
public class FioClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FioClient fioClient(FioClientSettings settings) {
        return new FioClient(settings);
    }
}
