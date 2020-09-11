package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.ItemInformation;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
class ItemInformationRepositoryTest {

    @Autowired
    ItemInformationRepository repo;

    @Test
    void creationTest(){
        ItemInformation testItem = ItemInformation.builder()
                .id("1")
                .name("Long-sword")
                .type(ItemInformation.Type.EQUIP).build();

        repo.insert(testItem).block();

        Mono<ItemInformation> result = repo.findById("1");

        StepVerifier
                .create(result)
                .assertNext(itemInformation -> {
                    log.info(itemInformation.toString());
                    assertEquals("Long-sword", itemInformation.getName());
                    assertEquals(ItemInformation.Type.EQUIP, itemInformation.getType());
                })
                .expectComplete()
                .verify();
    }
}