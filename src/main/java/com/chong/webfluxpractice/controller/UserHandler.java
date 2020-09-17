package com.chong.webfluxpractice.controller;

import com.chong.webfluxpractice.domain.ItemInformation;
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
    static class UserAndType{
        private String userId;
        private ItemInformation.Type type;
    }

    @Data
    static class UserAndItem{
        private String userId;
        private String itemId;
    }

    public Mono<ServerResponse> getItemList(ServerRequest request){
        Mono<String> userId = request.bodyToMono(String.class);
        return ServerResponse.ok().body(inventoryManager.getItemList(userId), ItemInformation.class);
    }

    public Mono<ServerResponse> getItemListByType(ServerRequest request){
        Mono<UserAndType> result = request.bodyToMono(UserAndType.class);
        return result.flatMap(userAndType -> ServerResponse
                .ok()
                .body(inventoryManager.getItemListByType(Mono.just(userAndType.getUserId()), userAndType.getType()), ItemInformation.class));
    }

    public Mono<ServerResponse> getItem(ServerRequest request){
        Mono<UserAndItem> result = request.bodyToMono(UserAndItem.class);
        return result.flatMap(userAndItem -> ServerResponse.ok().body(inventoryManager.getItem(Mono.just(userAndItem.getUserId()), userAndItem.getItemId()), String.class));
    }

    public Mono<ServerResponse> getItemCount(ServerRequest request){
        Mono<String> userId = request.bodyToMono(String.class);
        return ServerResponse.ok().body(inventoryManager.getItemCountByType(userId), Map.class);
    }

}
