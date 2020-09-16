package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Flux<User> findByServerName(String serverName);
}
