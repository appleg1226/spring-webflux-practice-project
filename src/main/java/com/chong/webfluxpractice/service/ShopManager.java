package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.Shop;
import com.chong.webfluxpractice.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShopManager {
    Flux<ItemInformation> getItemListforSell(Mono<Shop> shop);
    Mono<String> buyItemfromUser(Mono<Shop> shop, User user, ItemInformation item);
    Mono<String> sellItemtoUser(Mono<Shop> shop, User user, ItemInformation item);
}
