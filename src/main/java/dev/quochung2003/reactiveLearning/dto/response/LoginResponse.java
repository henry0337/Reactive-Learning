package dev.quochung2003.reactiveLearning.dto.response;

public record LoginResponse(
        String token,
        String refreshToken
) {}
