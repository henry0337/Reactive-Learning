package dev.quochung2003.reactiveLearning.config;

import dev.quochung2003.reactiveLearning.entity.Role;
import dev.quochung2003.reactiveLearning.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static dev.quochung2003.reactiveLearning.constant.JwtConstant.SECRET_KEY;
import static dev.quochung2003.reactiveLearning.constant.Endpoint.*;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    UserService service;

    String[] publicRoutes = {
            SHUTDOWN_ROUTE,
            AUTH_ROUTE + ALL_CHILD_ROUTE,
            USER_ROUTE + ALL_CHILD_ROUTE,
            SWAGGER_UI_1_ROUTE + ALL_CHILD_ROUTE,
            API_DOC_ROUTE + ALL_CHILD_ROUTE,
            SWAGGER_UI_2_ROUTE,
            LIST_ALL_ROUTE
    };

    String[] roleAuthorizeRoutes = {
            CHANGE_PASSWORD_ROUTE,
            USER_INFO_ROUTE
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final @NonNull ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers(publicRoutes).permitAll();
                    exchange.pathMatchers(roleAuthorizeRoutes).hasAnyAuthority(Role.USER.name(), Role.ADMIN.name());
                    exchange.anyExchange().authenticated();
                })
                .authenticationManager(reactiveAuthenticationManager())
                .oauth2ResourceServer(oauth2Spec -> oauth2Spec.jwt(Customizer.withDefaults()))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return http.build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        final var manager = new UserDetailsRepositoryReactiveAuthenticationManager(service.userDetailsService());
        manager.setPasswordEncoder(encoder());
        return manager;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        final var secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
