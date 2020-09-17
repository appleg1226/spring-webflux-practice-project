package com.chong.webfluxpractice.controller;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.service.AdminManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AdminHandler {

    private final AdminManager adminManager;

    @Data
    static class ServerAndItem{
        private String serverName;
        private String itemId;
    }

    public Mono<ServerResponse> provideAll(ServerRequest request){
        return request.bodyToMono(String.class)
                .flatMap(item -> ServerResponse
                .ok()
                .body(adminManager.provideItemtoAll(item), String.class));
    }

    public Mono<ServerResponse> providetoServer(ServerRequest request){
        return request.bodyToMono(ServerAndItem.class)
                .flatMap(serverAndItem -> ServerResponse
                        .ok()
                        .body(adminManager.provideItemtoServer(serverAndItem.getServerName(), serverAndItem.getItemId()), String.class));
    }
}
