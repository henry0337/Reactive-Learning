package dev.quochung2003.reactiveLearning.controller;

import dev.quochung2003.reactiveLearning.entity.User;
import dev.quochung2003.reactiveLearning.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static dev.quochung2003.reactiveLearning.constant.Endpoint.USER_ROUTE;

@RestController
@RequestMapping(USER_ROUTE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User")
public class UserController {
    UserService service;

    @GetExchange
    @ResponseStatus(HttpStatus.OK)
    public Flux<User> getAll() {
        return service.listAll();
    }

    @PostExchange
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> save(@RequestBody User user) {
        return service.save(user);
    }

    @PutExchange
    @ResponseStatus(HttpStatus.OK)
    public Mono<User> update(@RequestParam String email, @RequestBody User user) {
        return service.update(email, user);
    }

    @DeleteExchange
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@RequestParam String email) {
        return service.delete(email);
    }
}
