package dev.quochung2003.reactiveLearning.dto.request;

import dev.quochung2003.reactiveLearning.entity.Role;

public record RegisterRequest(
        String name,
        String email,
        String password,
        String avatar,
        Role role
) {
}
