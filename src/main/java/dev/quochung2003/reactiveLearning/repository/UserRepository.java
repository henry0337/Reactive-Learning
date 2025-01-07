package dev.quochung2003.reactiveLearning.repository;

import dev.quochung2003.reactiveLearning.entity.Role;
import dev.quochung2003.reactiveLearning.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Mono<User> findByEmail(final String email);
    Mono<User> findByRole(final Role role);
}
