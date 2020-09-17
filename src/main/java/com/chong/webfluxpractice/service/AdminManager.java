package com.chong.webfluxpractice.service;

import reactor.core.publisher.Mono;

public interface AdminManager {
    Mono<String> provideItemtoAll(String itemId);
    Mono<String> provideItemtoServer(String serverName, String itemId);
}
