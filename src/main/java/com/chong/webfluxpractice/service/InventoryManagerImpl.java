package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.domain.User;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class InventoryManagerImpl implements InventoryManager {

    private final UserRepository userRepo;

    @Override
    public Flux<ItemInformation> getItemList(Mono<User> userMono) {
        return userMono
            .flatMap(user -> userRepo.findById(user.getUserId()))
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
            .map(User::getInventory)
            .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<ItemInformation> getItemListByType(Mono<User> userMono, ItemInformation.Type type) {
        return userMono
            .flatMap(u -> userRepo.findById(u.getUserId()))
            .switchIfEmpty(Mono.empty())
            .map(User::getInventory)
            .flatMapMany(Flux::fromIterable)
            .filter(item -> ItemInformation.isType(item, type));
    }

    @Override
    public Mono<String> getItem(Mono<User> userMono, ItemInformation item) {
        return userMono
            .flatMap(user -> userRepo.findById(user.getUserId()))
            .switchIfEmpty(Mono.empty())
            .flatMap(user -> {
                List<ItemInformation> result = user.getInventory();
                result.add(item);
                user.setInventory(result);
                return userRepo.save(user);
            }).map(user -> user.getUserId() + " - received item: " + item.getName());
    }

    @Override
    public Flux<ItemInformation> checkEventItemsOwn(Mono<User> userMono) {
        WebClient webClient = WebClient.create();
        Flux<ItemInformation> eventItems = webClient
                .get()
                .uri("http://event_server/event_items")
                .retrieve()
                .bodyToFlux(ItemInformation.class);

        return userMono.flatMap(user -> userRepo.findById(user.getUserId()))
                 .switchIfEmpty(Mono.empty())
                 .flatMapIterable(User::getInventory)
                 .flatMap(itemInformation -> eventItems.filter(itemInformation1 -> itemInformation.getName().equals(itemInformation1.getName())));
    }

    @Override
    public Mono<Map<ItemInformation.Type, Long>> getItemCountByType(Mono<User> userMono) {
        return userMono
                .flatMap(user -> userRepo.findById(user.getUserId()))
                .switchIfEmpty(Mono.empty())
                .flatMap(user -> Flux.fromIterable(user.getInventory())
                        .groupBy(ItemInformation::getType)
                        .concatMap(groupedFlux-> groupedFlux.count()
                                .map(count ->{
                                    final Map<ItemInformation.Type, Long> typeCount = new HashMap<>();
                                    typeCount.put(groupedFlux.key(), count);
                                    return typeCount;
                                })).reduce((accumulatedMap, currentMap) -> {
                            Map<ItemInformation.Type, Long> typeCount = new HashMap<>();
                            typeCount.putAll(accumulatedMap);
                            typeCount.putAll(currentMap);
                            return typeCount;
                        }));

    }
}
