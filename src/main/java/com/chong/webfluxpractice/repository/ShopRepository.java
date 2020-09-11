package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.Shop;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ShopRepository extends ReactiveMongoRepository<Shop, String> {
}
