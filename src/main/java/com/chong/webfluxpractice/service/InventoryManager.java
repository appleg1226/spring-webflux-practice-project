package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface InventoryManager {
    Flux<ItemInformation> getItemList(Mono<String> userId);
    Flux<ItemInformation> getItemListByType(Mono<String> userId, ItemInformation.Type type);
    Mono<String> getItem(Mono<String> userId, ItemInformation item);
    Flux<ItemInformation> checkEventItemsOwn(Mono<String> userId);
    Mono<Map<ItemInformation.Type, Long>> getItemCountByType(Mono<String> userId);
}
