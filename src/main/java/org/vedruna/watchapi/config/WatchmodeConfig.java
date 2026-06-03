package org.vedruna.watchapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Clase de configuración para instanciar el cliente HTTP RestTemplate.
 */
@Configuration
public class WatchmodeConfig {

    /**
     * Define el bean RestTemplate para realizar llamadas externas.
     * 
     * @return Una instancia de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
