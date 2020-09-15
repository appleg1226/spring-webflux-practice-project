package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface InventoryManager {
    Flux<ItemInformation> getItemList(Mono<User> user);
    Flux<ItemInformation> getItemListByType(Mono<User> user, ItemInformation.Type type);
    Mono<String> getItem(Mono<User> user, ItemInformation item);
    Flux<ItemInformation> checkEventItemsOwn(Mono<User> user);
    Mono<Map<String, Integer>> getItemCountByType(Mono<User> user);
}
