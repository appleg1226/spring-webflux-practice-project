package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.ItemInformation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemInformationRepository extends ReactiveMongoRepository<ItemInformation, String> {

}
