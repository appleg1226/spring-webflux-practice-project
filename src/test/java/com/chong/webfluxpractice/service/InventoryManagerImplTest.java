package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
class InventoryManagerImplTest {

    @Autowired InventoryManager inventoryManager;
    @Autowired UserRepository userRepository;

    @Test
    void getItemListTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("glove").type(ItemInformation.Type.EQUIP).build();
        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3));

        User testUser = User.builder()
                .userId("1")
                .gold(0)
                .serverName("asia")
                .inventory(inventory).build();

        userRepository.insert(testUser).block();

        Flux<ItemInformation> fluxResult = inventoryManager.getItemList(Mono.just(testUser));
        fluxResult.subscribe(itemInformation -> log.info(itemInformation.toString()));

        StepVerifier.create(fluxResult)
                .consumeNextWith(itemInformation -> {          // consumeNextWith로 내부 원소 확인
                    log.info(itemInformation.toString());
                    log.info(i1.toString());
                    assertEquals(itemInformation, i1);
                })
//                .expectNext(i1)
                .expectNext(i2)
                .expectNext(i3)
                .verifyComplete();
    }
}