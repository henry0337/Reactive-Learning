package dev.quochung2003.reactiveLearning.service;

import dev.quochung2003.reactiveLearning.entity.User;
import dev.quochung2003.reactiveLearning.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository repository;

    public Flux<User> listAll() {
        return repository.findAll();
    }

    public Mono<User> save(final User user) {
        return repository.save(user);
    }

    public Mono<User> update(final @NonNull String email, final User credential) {
        if (email.isBlank()) {
            log.error("Email field is currently blank!");
            throw new IllegalArgumentException();
        }

        return repository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with email: " + email)))
                .flatMap(existingUser -> {
                    existingUser.setName(credential.getName());
                    existingUser.setPassword(credential.getPassword());
                    existingUser.setAvatar(credential.getAvatar());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existingUser);
                });
    }

    public Mono<Void> delete(@NonNull final String email) {
        if (email.isBlank()) {
            log.error("Email field is currently blank!");
            throw new NullPointerException();
        }

        return repository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with email: " + email)))
                .flatMap(repository::delete);
    }

    public ReactiveUserDetailsService userDetailsService() {
        // Lý giải việc tại sao phải dùng flatMap() ở đoạn này:
        // Nói đơn giản, là do cơ chế covariant của Generic, findByEmail() mặc dù trả về Mono<User> mà User lại là lớp con triển khai
        // UserDetails, đáng lẽ ra thì hợp lệ do cần trả về Mono<UserDetails> nhưng trình biên dịch chưa thể hiểu đoạn này
        // => flatMap() là một cách để nói cho trình biên dịch xử lý covariant và contravariant tốt hơn.
        return email -> repository.findByEmail(email).flatMap(user -> Mono.just(user));
    }
}
