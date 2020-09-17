package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.Shop;
import com.chong.webfluxpractice.repository.ItemInformationRepository;
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
    private final ItemInformationRepository itemRepository;

    @Override
    public Flux<ItemInformation> getItemListforSell(Mono<String> shopId) {
        return shopId
                .flatMap(shopRepository::findById)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .map(Shop::getItemList)
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<String> buyItemfromUser(String shopId, String userId, String itemId) {
        return Mono.just(itemId)
                .flatMap(itemRepository::findById)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .flatMap(item -> userRepository.findById(userId)
                                .flatMap(user->{
                                    List<ItemInformation> userItemList = user.getInventory();
                                    userItemList.remove(item);
                                    user.setInventory(userItemList);
                                    log.info(user.toString());
                                    return userRepository.save(user);
                                })
                                .flatMap(user -> shopRepository.findById(shopId))
                                .flatMap(shop -> {
                                    List<ItemInformation> shopItemList = shop.getItemList();
                                    shopItemList.add(item);
                                    shop.setItemList(shopItemList);
                                    log.info(shop.toString());
                                    return shopRepository.save(shop);
                        })).map(shop -> userId + " sold item - " + itemId);
    }

    @Override
    public Mono<String> sellItemtoUser(String shopId, String userId, String itemId) {
        return Mono.just(itemId)
                .flatMap(itemRepository::findById)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .flatMap(item -> userRepository.findById(userId)
                        .flatMap(user->{
                            List<ItemInformation> userItemList = user.getInventory();
                            userItemList.add(item);
                            user.setInventory(userItemList);
                            log.info(user.toString());
                            return userRepository.save(user);
                        })
                        .flatMap(user -> shopRepository.findById(shopId))
                        .flatMap(shop -> {
                            List<ItemInformation> shopItemList = shop.getItemList();
                            shopItemList.remove(item);
                            shop.setItemList(shopItemList);
                            log.info(shop.toString());
                            return shopRepository.save(shop);
                        })).map(shop -> userId + " purchased item - " + itemId);

    }
}
