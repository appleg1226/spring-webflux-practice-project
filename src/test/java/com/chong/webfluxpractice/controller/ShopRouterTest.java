package com.chong.webfluxpractice.controller;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.Shop;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.ItemInformationRepository;
import com.chong.webfluxpractice.repository.ShopRepository;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class ShopRouterTest {

    private WebTestClient webTestClient;
    private int shopCount = 0;

    @Autowired private UserRepository userRepository;
    @Autowired private ShopRepository shopRepository;
    @Autowired private ItemInformationRepository itemRepository;

    @Autowired private ShopRouter shopRouter;
    @Autowired private ShopHandler shopHandler;

    @BeforeAll
    public void beforeAllTest(){
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();
        itemRepository.insert(Arrays.asList(i1, i2, i3, i4, i5)).blockLast();
    }

    @BeforeEach
    public void beforeTest(){
        webTestClient = WebTestClient.bindToRouterFunction(shopRouter.routeByShop(shopHandler)).build();
        shopCount = shopCount + 1;

        // 사전 정보 저장
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();

        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3));
        List<ItemInformation> inventory2 = new ArrayList<>(Arrays.asList(i4, i5));

        User testUser = User.builder().userId(String.valueOf(shopCount)).gold(0).serverName("asia").inventory(inventory).build();
        Shop testShop = Shop.builder().id(String.valueOf(shopCount)).itemList(inventory2).build();

        shopRepository.insert(testShop).block();
        userRepository.insert(testUser).block();
    }

    @Test
    @DisplayName("상점 아이템 목록 반환 api call")
    public void shopItemListCallTest(){
        webTestClient.get()
                .uri("/shop/items/" + shopCount)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemInformation.class)
                .consumeWith(r -> {
                    log.info(r.getResponseBody().toString());
                });
    }

    @Test
    @DisplayName("아이템 매입 api call")
    public void sellToUserCallTest(){
        ShopHandler.ShopAndUserAndItem shopAndUserAndItem = new ShopHandler.ShopAndUserAndItem();
        shopAndUserAndItem.setUserId(String.valueOf(shopCount));
        shopAndUserAndItem.setShopId(String.valueOf(shopCount));
        shopAndUserAndItem.setItemId("3");

        webTestClient.post()
                .uri("/shop/purchase")
                .body(Mono.just(shopAndUserAndItem), ShopHandler.ShopAndUserAndItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(r -> {
                    log.info(Objects.requireNonNull(r.getResponseBody()));
                });

        userRepository.findById(String.valueOf(shopCount)).subscribe(user -> log.info(user.toString()));
        shopRepository.findById(String.valueOf(shopCount)).subscribe(shop -> log.info(shop.toString()));
    }

    @Test
    @DisplayName("유저에게 아이템 판매 api call")
    public void purchaseFromUserCallTest(){
        ShopHandler.ShopAndUserAndItem shopAndUserAndItem = new ShopHandler.ShopAndUserAndItem();
        shopAndUserAndItem.setUserId(String.valueOf(shopCount));
        shopAndUserAndItem.setShopId(String.valueOf(shopCount));
        shopAndUserAndItem.setItemId("5");

        webTestClient.post()
                .uri("/shop/sell")
                .body(Mono.just(shopAndUserAndItem), ShopHandler.ShopAndUserAndItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(r -> {
                    log.info(Objects.requireNonNull(r.getResponseBody()));
                });

        userRepository.findById(String.valueOf(shopCount)).subscribe(user -> log.info(user.toString()));
        shopRepository.findById(String.valueOf(shopCount)).subscribe(shop -> log.info(shop.toString()));
    }

}