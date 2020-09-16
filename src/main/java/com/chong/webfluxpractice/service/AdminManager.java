package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import reactor.core.publisher.Mono;

public interface AdminManager {
    Mono<String> provideItemtoAll(ItemInformation item);
    Mono<String> provideItemtoServer(String serverName, ItemInformation item);
}
