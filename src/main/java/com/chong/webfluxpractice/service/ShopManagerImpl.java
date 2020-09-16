package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.Shop;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.ShopRepository;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
@Log
public class ShopManagerImpl implements ShopManager {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Override
    public Flux<ItemInformation> getItemListforSell(Mono<Shop> shopMono) {
        return shopMono
                .flatMap(shop -> shopRepository.findById(shop.getId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .map(Shop::getItemList)
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<String> buyItemfromUser(Mono<Shop> shopMono, User user, ItemInformation item) {
        return shopMono
                .flatMap(shop -> shopRepository.findById(shop.getId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .flatMap(shop -> userRepository.findById(user.getUserId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                        .flatMap(user1 ->{
                            List<ItemInformation> userItemList = user1.getInventory();
                            userItemList.remove(item);
                            user1.setInventory(userItemList);
                            log.info(user1.toString());
                            return userRepository.save(user1);
                        })
                        .flatMap(user1 -> {
                            List<ItemInformation> shopItemList = shop.getItemList();
                            shopItemList.add(item);
                            shop.setItemList(shopItemList);
                            log.info(shop.toString());
                            return shopRepository.save(shop);
                        })).map(shop -> user.getUserId() + " sold item - " + item.getName());
    }

    @Override
    public Mono<String> sellItemtoUser(Mono<Shop> shopMono, User user, ItemInformation item) {
        return shopMono
                .flatMap(shop -> shopRepository.findById(shop.getId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .flatMap(shop -> userRepository.findById(user.getUserId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                        .flatMap(user1 ->{
                            List<ItemInformation> userItemList = user1.getInventory();
                            userItemList.add(item);
                            user1.setInventory(userItemList);
                            log.info(user1.toString());
                            return userRepository.save(user1);
                        })
                        .flatMap(user1 -> {
                            List<ItemInformation> shopItemList = shop.getItemList();
                            shopItemList.remove(item);
                            shop.setItemList(shopItemList);
                            log.info(shop.toString());
                            return shopRepository.save(shop);
                        })).map(shop -> user.getUserId() + " purchased item - " + item.getName());

    }
}
