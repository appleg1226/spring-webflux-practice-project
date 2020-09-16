package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.Shop;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Log
class ShopRepositoryTest {

    @Autowired
    ShopRepository repository;

    @Test
    void creationTest(){
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("glove").type(ItemInformation.Type.EQUIP).build();

        Shop tempShop = Shop.builder().id("first shop").itemList(Arrays.asList(i1, i2, i3)).build();

        repository.insert(tempShop).block();
        Mono<Shop> result = repository.findById("first shop");

        StepVerifier.create(result)
                .assertNext(shop -> {
                    log.info(shop.toString());
                    assertIterableEquals(Arrays.asList(i1, i2, i3), shop.getItemList());
                })
                .verifyComplete();
    }
}