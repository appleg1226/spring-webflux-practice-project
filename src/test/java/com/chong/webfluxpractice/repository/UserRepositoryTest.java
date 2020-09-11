package com.chong.webfluxpractice.repository;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Log
@DataMongoTest
class UserRepositoryTest {

    @Autowired
    UserRepository repo;

    @Test
    void creationTest(){
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("glove").type(ItemInformation.Type.EQUIP).build();

        User user1 = User.builder()
                .userId("123")
                .gold(0)
                .serverName("maple")
                .inventory(Arrays.asList(i1, i2, i3))
                .build();

        repo.insert(user1).block();
        Mono<User> result = repo.findById(Mono.just("123"));


        StepVerifier.create(result)
                .assertNext(user -> {
                    assertIterableEquals(Arrays.asList(i1, i2, i3), user.getInventory());
                    assertEquals(0, user.getGold());
                    assertEquals("maple", user.getServerName());
                })
                .expectComplete()
                .verify();
    }
}