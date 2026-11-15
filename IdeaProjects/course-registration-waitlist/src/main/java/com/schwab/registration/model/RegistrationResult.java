package com.schwab.registration.model;

public record RegistrationResult(
        String learnerId,
        RegistrationStatus status,
        String message,
        String promotedLearnerId) {
}
