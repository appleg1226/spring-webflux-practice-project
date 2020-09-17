package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.repository.ItemInformationRepository;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminManagerImpl implements AdminManager {

    private final UserRepository userRepository;
    private final ItemInformationRepository itemRepository;

    @Override
    public Mono<String> provideItemtoAll(String itemId) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .flatMap(item -> userRepository.findAll()
                            .flatMap(user -> {
                                List<ItemInformation> userItems = user.getInventory();
                                userItems.add(item);
                                user.setInventory(userItems);
                                return userRepository.save(user);
                            })
                            .collect(Collectors.counting())
                            .map(Object::toString));
    }

    @Override
    public Mono<String> provideItemtoServer(String serverName, String itemId) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                .flatMap(item -> userRepository.findByServerName(serverName)
                        .flatMap(user -> {
                            List<ItemInformation> userItems = user.getInventory();
                            userItems.add(item);
                            user.setInventory(userItems);
                            return userRepository.save(user);
                        })
                        .collect(Collectors.counting())
                        .map(Object::toString));
    }
}
