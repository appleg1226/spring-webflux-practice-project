package com.chong.webfluxpractice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AdminRouter {

    @Bean
    public RouterFunction<ServerResponse> routeByAdmin(AdminHandler adminHandler){
        return nest(path("/admin"),
                route(POST("/give/all"), adminHandler::provideAll)
                .andRoute(POST("/give/server"), adminHandler::providetoServer));
    }
}
