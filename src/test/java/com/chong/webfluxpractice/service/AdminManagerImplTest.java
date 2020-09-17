package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.ItemInformationRepository;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
class AdminManagerImplTest {

    @Autowired
    AdminManager adminManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemInformationRepository itemRepository;

    @Test
    void provideItemtoAllTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        List<ItemInformation> inventory = new ArrayList<>();

        User testUser1 = User.builder().userId("1").gold(0).serverName("asia").inventory(inventory).build();
        User testUser2 = User.builder().userId("2").gold(0).serverName("asia").inventory(inventory).build();
        User testUser3 = User.builder().userId("3").gold(0).serverName("asia").inventory(inventory).build();

        userRepository.saveAll(Arrays.asList(testUser1, testUser2, testUser3)).blockFirst();
        itemRepository.save(i1).block();

        adminManager.provideItemtoAll(i1.getId()).block();

        userRepository.findAll().collect(Collectors.toList()).subscribe(user -> log.info(user.toString()));
    }


    @Test
    void provideItemtoServerTest() {
        ItemInformation i1 = ItemInformation.builder().id("1").name("sword").type(ItemInformation.Type.EQUIP).build();
        List<ItemInformation> inventory = new ArrayList<>();

        User testUser1 = User.builder().userId("1").gold(0).serverName("asia").inventory(inventory).build();
        User testUser2 = User.builder().userId("2").gold(0).serverName("asia").inventory(inventory).build();
        User testUser3 = User.builder().userId("3").gold(0).serverName("europe").inventory(inventory).build();

        userRepository.saveAll(Arrays.asList(testUser1, testUser2, testUser3)).blockFirst();
        itemRepository.save(i1).block();

        adminManager.provideItemtoServer("asia", i1.getId()).block();

        userRepository.findAll().subscribe(user -> {
            log.info(user.toString());
        });
    }
}