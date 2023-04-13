package org.cdaz.representation.config;

import org.cdaz.representation.interceptor.WebSocketInterceptor;
import org.cdaz.representation.service.impl.RepresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private RepresentationService representationService;

    @Autowired
    private WebSocketInterceptor webSocketInterceptor;

    @Bean
    public WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(representationService, "ws")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
