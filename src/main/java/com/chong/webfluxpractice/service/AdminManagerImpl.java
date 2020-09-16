package com.chong.webfluxpractice.service;

import com.chong.webfluxpractice.domain.ItemInformation;
import com.chong.webfluxpractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminManagerImpl implements AdminManager {

    private final UserRepository userRepository;

    @Override
    public Mono<String> provideItemtoAll(ItemInformation item) {
        return userRepository.findAll()
                .flatMap(user -> {
                    List<ItemInformation> userItems = user.getInventory();
                    userItems.add(item);
                    user.setInventory(userItems);
                    return userRepository.save(user);
                }).collect(Collectors.counting()).map(Object::toString);
    }

    @Override
    public Mono<String> provideItemtoServer(String serverName, ItemInformation item) {
        return userRepository.findByServerName(serverName)
                .flatMap(user -> {
                    List<ItemInformation> userItems = user.getInventory();
                    userItems.add(item);
                    user.setInventory(userItems);
                    return userRepository.save(user);
                }).collect(Collectors.counting()).map(Object::toString);
    }
}
