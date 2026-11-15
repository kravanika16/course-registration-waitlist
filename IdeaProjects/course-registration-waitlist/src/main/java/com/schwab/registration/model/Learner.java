package com.schwab.registration.model;

public record Learner(String learnerId, boolean prerequisiteSatisfied) {
    public Learner {
        if (learnerId == null || learnerId.isBlank()) {
            throw new IllegalArgumentException("Learner ID cannot be blank");
        }
    }
}
