package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.Shop;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.ShopRepository;
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
class ShopManagerImplTest {

    @Autowired
    ShopManager shopManager;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void getItemListforSellTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("glove").type(ItemInformation.Type.EQUIP).build();
        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3));

        Shop testShop = Shop.builder().id("1").itemList(inventory).build();
        shopRepository.insert(testShop).block();

        Flux<ItemInformation> fluxResult = shopManager.getItemListforSell(Mono.just(testShop));
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
    void buyItemfromUserTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();

        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3));
        List<ItemInformation> inventory2 = new ArrayList<>(Arrays.asList(i4, i5));

        User testUser = User.builder().userId("1").gold(0).serverName("asia").inventory(inventory).build();
        Shop testShop = Shop.builder().id("1").itemList(inventory2).build();

        shopRepository.insert(testShop).block();
        userRepository.insert(testUser).block();

        shopManager.buyItemfromUser(Mono.just(testShop), testUser, i3).block();

        userRepository.findById(testUser.getUserId()).subscribe(user -> {
            log.info(user.getInventory().toString());
            assertEquals(inventory.size()-1, user.getInventory().size());
        });

        shopRepository.findById(testShop.getId()).subscribe(shop -> {
            log.info(shop.getItemList().toString());
            assertEquals(inventory2.size()+1, shop.getItemList().size());
        });
    }

    @Test
    void sellItemtoUserTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();

        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3));
        List<ItemInformation> inventory2 = new ArrayList<>(Arrays.asList(i4, i5));

        User testUser = User.builder().userId("1").gold(0).serverName("asia").inventory(inventory).build();
        Shop testShop = Shop.builder().id("1").itemList(inventory2).build();

        shopRepository.insert(testShop).block();
        userRepository.insert(testUser).block();

        shopManager.sellItemtoUser(Mono.just(testShop), testUser, i4).block();

        userRepository.findById(testUser.getUserId()).subscribe(user -> {
            log.info(user.getInventory().toString());
            assertEquals(inventory.size()+1, user.getInventory().size());
        });

        shopRepository.findById(testShop.getId()).subscribe(shop -> {
            log.info(shop.getItemList().toString());
            assertEquals(inventory2.size()-1, shop.getItemList().size());
        });
    }
}