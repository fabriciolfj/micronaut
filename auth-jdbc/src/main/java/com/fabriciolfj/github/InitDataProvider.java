package com.fabriciolfj.github;

import com.fabriciolfj.github.auth.persistence.UserEntity;
import com.fabriciolfj.github.auth.persistence.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
public class InitDataProvider {

    private final UserRepository userRepository;

    @EventListener
    public void init(final StartupEvent event) {
        final String email = "fabricio@outlook.com";

        if (userRepository.findByEmail(email).isEmpty()) {
            final var user = UserEntity.builder()
                    .email(email)
                    .password("12345")
                    .build();
            userRepository.save(user);
        }
    }
}
