package com.chong.webfluxpractice.controller;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.service.InventoryManager;
import lombok.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserHandler {

    private final InventoryManager inventoryManager;

    @Data
    static class UserAndItem{
        private String userId;
        private String itemId;
    }

    public Mono<ServerResponse> getItemList(ServerRequest request){
        String userId = request.pathVariable("id");
        return ServerResponse.ok().body(inventoryManager.getItemList(Mono.just(userId)), ItemInformation.class);
    }

    public Mono<ServerResponse> getItemListByType(ServerRequest request){
        String userId = request.pathVariable("id");
        String type = request.pathVariable("type");
        ItemInformation.Type itemType= ItemInformation.Type.valueOf(type);

        return ServerResponse.ok()
                .body(inventoryManager.getItemListByType(Mono.just(userId), itemType), ItemInformation.class);
    }

    public Mono<ServerResponse> getItem(ServerRequest request){
        Mono<UserAndItem> result = request.bodyToMono(UserAndItem.class);
        return result.flatMap(userAndItem -> ServerResponse.ok().body(inventoryManager.getItem(Mono.just(userAndItem.getUserId()), userAndItem.getItemId()), String.class));
    }

    public Mono<ServerResponse> getItemCount(ServerRequest request){
        String userId = request.pathVariable("id");
        return ServerResponse.ok().body(inventoryManager.getItemCountByType(Mono.just(userId)), Map.class);
    }
}
