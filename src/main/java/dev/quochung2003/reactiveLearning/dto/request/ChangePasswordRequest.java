package dev.quochung2003.reactiveLearning.dto.request;

public record ChangePasswordRequest(
        String email,
        String authString,
        String newPassword
) {}
