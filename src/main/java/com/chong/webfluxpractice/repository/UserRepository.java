package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
