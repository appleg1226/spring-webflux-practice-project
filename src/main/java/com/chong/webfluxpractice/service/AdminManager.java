package com.chong.webfluxpractice.service;

import reactor.core.publisher.Mono;

public interface AdminManager {
    Mono<String> provideItemtoAll();
    Mono<String> provideItemtoServer(String serverName);
}
