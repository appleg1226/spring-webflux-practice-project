package com.chong.webfluxpractice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> routeByUser(UserHandler userHandler){
        return nest(path("/user"),
                route(GET("/items/{id}"), userHandler::getItemList)
                .andRoute(GET("/items/{type}/{id}"), userHandler::getItemListByType)
                .andRoute(GET("/count/{id}"), userHandler::getItemCount)
                .andRoute(POST("/get"), userHandler::getItem));
    }
}
