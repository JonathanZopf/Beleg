package org.hszg.beleg;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final ServerProperties serverProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        var openAPI = new OpenAPI();
        openAPI.addServersItem(createServerWithUrl("http://localhost:" + this.serverProperties.getPort()));
        return openAPI;
    }

    private static Server createServerWithUrl(String value) {
        return new Server().url(value);
    }
}