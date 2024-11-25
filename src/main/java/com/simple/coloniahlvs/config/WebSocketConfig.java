package com.simple.coloniahlvs.config;
import com.simple.coloniahlvs.websocket.WebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketController webSocketController;

    public WebSocketConfig(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketController, "/ws/servo").setAllowedOrigins("*");
    }

}