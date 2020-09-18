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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminRouterTest {

    private WebTestClient webTestClient;

    @Autowired private UserRepository userRepository;
    @Autowired private ItemInformationRepository itemRepository;

    @Autowired private AdminRouter adminRouter;
    @Autowired private AdminHandler adminHandler;

    @BeforeAll
    public void beforeAllTest(){
        webTestClient = WebTestClient.bindToRouterFunction(adminRouter.routeByAdmin(adminHandler)).build();

        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();
        itemRepository.insert(Arrays.asList(i1, i2, i3, i4, i5)).blockLast();

        List<ItemInformation> inventory = new ArrayList<>();
        User testUser1 = User.builder().userId("1").gold(0).serverName("asia").inventory(inventory).build();
        User testUser2 = User.builder().userId("2").gold(0).serverName("asia").inventory(inventory).build();
        User testUser3 = User.builder().userId("3").gold(0).serverName("europe").inventory(inventory).build();
        userRepository.saveAll(Arrays.asList(testUser1, testUser2, testUser3)).blockFirst();
    }

    @Test
    @DisplayName("모든 유저에게 아이템 배포 api call")
    @Order(1)
    public void giveToAllUserCallTest(){

        webTestClient.post()
                .uri("/admin/give/all")
                .body(Mono.just("1"), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(r -> {
                    log.info("배포한 유저 수");
                    log.info(Objects.requireNonNull(r.getResponseBody()));
                });

        userRepository.findAll().subscribe(user -> log.info(user.toString()));
    }

    @Test
    @DisplayName("특정 서버 유저에게 아이템 배포 api call")
    @Order(2)
    public void giveToServerUserCallTest(){
        AdminHandler.ServerAndItem serverAndItem = new AdminHandler.ServerAndItem();
        serverAndItem.setServerName("asia");
        serverAndItem.setItemId("5");

        webTestClient.post()
                .uri("/admin/give/server")
                .body(Mono.just(serverAndItem), AdminHandler.ServerAndItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(r -> {
                    log.info("배포한 유저 수");
                    log.info(Objects.requireNonNull(r.getResponseBody()));
                });

        userRepository.findAll().subscribe(user -> log.info(user.toString()));
    }
}