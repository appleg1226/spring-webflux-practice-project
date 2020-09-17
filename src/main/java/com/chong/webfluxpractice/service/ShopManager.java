package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShopManager {
    Flux<ItemInformation> getItemListforSell(Mono<String> shop);
    Mono<String> buyItemfromUser(String shopId, String userId, String itemId);
    Mono<String> sellItemtoUser(String shopId, String userId, String itemId);
}
