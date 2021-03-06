package com.chong.webfluxpractice.controller;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.ItemInformationRepository;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class UserRouterTest {

    private WebTestClient webTestClient;
    private int userCount = 0;

    @Autowired private UserRepository userRepository;
    @Autowired private UserRouter userRouter;
    @Autowired private UserHandler userHandler;
    @Autowired private ItemInformationRepository itemRepository;

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
        webTestClient = WebTestClient.bindToRouterFunction(userRouter.routeByUser(userHandler)).build();
        userCount = userCount + 1;

        // 유저 정보 저장
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i2 = ItemInformation.builder().id("2").name("armor").type(ItemInformation.Type.EQUIP).build();
        ItemInformation i3 = ItemInformation.builder().id("3").name("hp_portion").type(ItemInformation.Type.CONSUME).build();
        ItemInformation i4 = ItemInformation.builder().id("4").name("PHONE").type(ItemInformation.Type.OTHER).build();
        ItemInformation i5 = ItemInformation.builder().id("5").name("bitcoin").type(ItemInformation.Type.OTHER).build();

        List<ItemInformation> inventory = new ArrayList<>(Arrays.asList(i1, i2, i3, i4, i5));

        User testUser = User.builder()
                .userId(String.valueOf(userCount))
                .gold(0)
                .serverName("asia")
                .inventory(inventory).build();

        userRepository.insert(testUser).block();
    }

    @Test
    @DisplayName("가지고 있는 아이템 목록 반환 api call")
    public void userItemListCallTest(){
        webTestClient.get()
                .uri("/user/items/" + userCount)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemInformation.class)
                .consumeWith(r -> {
                    log.info(r.getResponseBody().toString());
                });
    }

    @Test
    @DisplayName("타입으로 가지고 있는 아이템 목록 반환 api call")
    public void userItemListByTypeCallTest(){
        webTestClient.get()
                .uri("/user/items/" + "EQUIP/" + userCount)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemInformation.class)
                .consumeWith(r -> {
                    log.info(Objects.requireNonNull(r.getResponseBody()).toString());
                });
    }

    @Test
    @DisplayName("타입별 아이템 갯수 반환 api call")
    public void userItemTypeCountCallTest(){
        webTestClient.get()
                .uri("/user/count/" + userCount)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(r -> {
                    log.info(Objects.requireNonNull(r.getResponseBody()).toString());
                });
    }

    @Test
    @DisplayName("아이템 획득 api call")
    public void userGetItemCallTest(){
        UserHandler.UserAndItem userAndItem = new UserHandler.UserAndItem();
        userAndItem.setUserId("1");
        userAndItem.setItemId("5");

        webTestClient.post()
                .uri("/user/get")
                .body(Mono.just(userAndItem), UserHandler.UserAndItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(r -> {
                    log.info(Objects.requireNonNull(r.getResponseBody()));
                });

        userRepository.findById("1").subscribe(user -> log.info(user.toString()));
    }

}