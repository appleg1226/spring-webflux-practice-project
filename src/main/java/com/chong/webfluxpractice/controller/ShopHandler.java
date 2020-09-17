package com.chong.webfluxpractice.controller;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.service.ShopManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class ShopHandler {

    final private ShopManager shopManager;

    @Data
    static class ShopAndUserAndItem{
        private String shopId;
        private String userId;
        private String itemId;
    }

    public Mono<ServerResponse> getItemList(ServerRequest request){
        Mono<String> shopId = request.bodyToMono(String.class);
        return ServerResponse.ok().body(shopManager.getItemListforSell(shopId), ItemInformation.class);
    }

    public Mono<ServerResponse> sellItem(ServerRequest request){
        Mono<ShopAndUserAndItem> result = request.bodyToMono(ShopAndUserAndItem.class);

        return result.flatMap(shopAndUserAndItem -> ServerResponse
                .ok()
                .body(shopManager.buyItemfromUser(shopAndUserAndItem.getShopId(), shopAndUserAndItem.getUserId(), shopAndUserAndItem.getItemId()), String.class));
    }

    public Mono<ServerResponse> purchaseItem(ServerRequest request){
        Mono<ShopAndUserAndItem> result = request.bodyToMono(ShopAndUserAndItem.class);
        return result.flatMap(shopAndUserAndItem -> ServerResponse
                .ok()
                .body(shopManager.sellItemtoUser(shopAndUserAndItem.getShopId(), shopAndUserAndItem.getUserId(), shopAndUserAndItem.getItemId()), String.class));
    }
}
