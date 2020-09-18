package com.chong.webfluxpractice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ShopRouter {

    @Bean
    public RouterFunction<ServerResponse> routeByShop(ShopHandler shopHandler){
        return nest(path("shop"),
                route(GET("/items/{id}"), shopHandler::getItemList)
                .andRoute(POST("/sell"), shopHandler::sellItem)
                .andRoute(POST("/purchase"), shopHandler::purchaseItem));
    }

}
