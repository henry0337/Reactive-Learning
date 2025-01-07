package dev.quochung2003.reactiveLearning.controller;

import dev.quochung2003.reactiveLearning.dto.request.ChangePasswordRequest;
import dev.quochung2003.reactiveLearning.dto.request.LoginRequest;
import dev.quochung2003.reactiveLearning.dto.request.RegisterRequest;
import dev.quochung2003.reactiveLearning.dto.response.LoginResponse;
import dev.quochung2003.reactiveLearning.entity.User;
import dev.quochung2003.reactiveLearning.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

import static dev.quochung2003.reactiveLearning.constant.Endpoint.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication")
public class AuthController {
    AuthService service;

    @PostExchange(REGISTER_ROUTE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<?> register(final @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostExchange(LOGIN_ROUTE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<?> login(final @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostExchange(CHANGE_PASSWORD_ROUTE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<LoginResponse> changePassword(final @RequestBody ChangePasswordRequest request) {
        return service.changePassword(request);
    }

    // Bug note: This method will currently emit error if you don't fill ALL fields (1 in Swagger, 1 is the "token" variable)
    // I will try to fix that asap.
    @GetExchange(USER_INFO_ROUTE)
    @Operation(security = {@SecurityRequirement(name = "Bearer Token")})
    public Mono<User> obtainUserCredential(final @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Mono<String> obtainedToken;

        // Bug is around here
        if (token != null && token.startsWith("Bearer ")) {
            obtainedToken = Mono.just(token.substring(7));
        } else {
            obtainedToken = extractJwtTokenFromSecurityContext();
        }

        return obtainedToken.flatMap(obtainedToken1 -> {
            if (obtainedToken1 == null || obtainedToken1.isEmpty()) {
                return Mono.error(new IllegalArgumentException("No valid token provided"));
            }
            return service.obtainUserCredential(obtainedToken1);
        });
    }

    @NonNull
    private Mono<String> extractJwtTokenFromSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication instanceof BearerTokenAuthenticationToken tokenAuth) {
                        return Mono.justOrEmpty(tokenAuth.getToken());
                    }
                    return Mono.empty();
                });
    }
}