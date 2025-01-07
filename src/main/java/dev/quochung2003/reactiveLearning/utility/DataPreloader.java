package dev.quochung2003.reactiveLearning.utility;

import dev.quochung2003.reactiveLearning.entity.Role;
import dev.quochung2003.reactiveLearning.entity.User;
import dev.quochung2003.reactiveLearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataPreloader implements CommandLineRunner {

    UserRepository repository;

    @Override
    public void run(String... args) {
        final Mono<Void> createAdmin = repository.findByRole(Role.ADMIN)
                .switchIfEmpty(
                        repository.save(
                                User.builder()
                                        .name("Administrator")
                                        .email("admin@email.com")
                                        .password(new BCryptPasswordEncoder().encode("admin"))
                                        .role(Role.ADMIN)
                                        .build()
                        )
                )
                .then();

        final Mono<Void> createUser = repository.findByRole(Role.USER)
                .switchIfEmpty(
                        repository.save(
                                User.builder()
                                        .name("Local user")
                                        .email("user@email.com")
                                        .password(new BCryptPasswordEncoder().encode("user"))
                                        .role(Role.USER)
                                        .build()
                        )
                )
                .then();

        Mono.when(createAdmin, createUser).block();
    }
}
