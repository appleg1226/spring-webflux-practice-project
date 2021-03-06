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
import java.util.Map;

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

        Flux<ItemInformation> fluxResult = inventoryManager.getItemList(Mono.just("1"));
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

    @Test
    void getItemListByTypeTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3));

        User testUser = User.builder()
                .userId("1")
                .gold(0)
                .serverName("asia")
                .inventory(inventory).build();

        userRepository.insert(testUser).block();

        Flux<ItemInformation> fluxResult = inventoryManager.getItemListByType(Mono.just("1"), ItemInformation.Type.EQUIP);
        fluxResult.subscribe(itemInformation -> log.info(itemInformation.toString()));

        StepVerifier.create(fluxResult)
                .consumeNextWith(itemInformation -> {          // consumeNextWith로 내부 원소 확인
                    log.info(itemInformation.toString());
                    log.info(i1.toString());
                    assertEquals(itemInformation, i1);
                })
//                .expectNext(i1)
                .expectNext(i2)
                .expectComplete()
                .verify();
    }

    @Test
    void getItemTest(){
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2));

        User testUser = User.builder()
                .userId("1")
                .gold(0)
                .serverName("asia")
                .inventory(inventory).build();

        userRepository.insert(testUser).block();

        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();

        Mono<String> stringMono = inventoryManager.getItem(Mono.just(testUser.getUserId()), i3.getId());
        StepVerifier.create(stringMono)
                .consumeNextWith(log::info).verifyComplete();
    }

    @Test
    void getItemCountTest(){
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();

        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3, i4, i5));

        User testUser = User.builder()
                .userId("1")
                .gold(0)
                .serverName("asia")
                .inventory(inventory).build();

        userRepository.insert(testUser).block();

        Mono<Map<ItemInformation.Type, Long>> result = inventoryManager.getItemCountByType(Mono.just(testUser.getUserId()));

        StepVerifier.create(result)
                .consumeNextWith(typeLongMap -> {
                    typeLongMap.forEach((type, aLong) -> {
                        log.info(type.toString() + ": " + aLong.toString());
                    });
                }).verifyComplete();
    }

    @Test
    void testFilter(){
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();

        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3, i4, i5));

        User testUser = User.builder()
                .userId("1")
                .gold(0)
                .serverName("asia")
                .inventory(inventory).build();

        userRepository.insert(testUser).block();

        Flux<ItemInformation> eventItems = Flux.fromIterable(Arrays.asList(i4, i5));
        Mono<User> userMono = Mono.just(testUser);

        Flux<ItemInformation> result = userMono.flatMap(user -> userRepository.findById(user.getUserId()))
                .switchIfEmpty(Mono.empty())
                .flatMapIterable(User::getInventory)
                .flatMap(itemInformation -> eventItems.filter(itemInformation1 -> itemInformation.getName().equals(itemInformation1.getName())));

        StepVerifier.create(result)
                .thenConsumeWhile(p->{
                    log.info(p.toString());
                    return true;
                }).verifyComplete();
    }
}