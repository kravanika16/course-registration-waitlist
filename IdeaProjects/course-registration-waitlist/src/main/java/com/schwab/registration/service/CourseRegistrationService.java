package com.schwab.registration.service;

import com.schwab.registration.model.Learner;
import com.schwab.registration.model.RegistrationResult;
import com.schwab.registration.model.RegistrationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseRegistrationService {

    private final int capacity;
    private final Map<String, Learner> enrolledLearners = new LinkedHashMap<>();
    private final Map<String, Learner> waitlistedLearners = new LinkedHashMap<>();

    public CourseRegistrationService(
            @Value("${course.capacity:2}") int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Course capacity must be greater than zero");
        }

        this.capacity = capacity;
    }

    public synchronized RegistrationResult register(Learner learner) {
        if (learner == null) {
            throw new IllegalArgumentException("Learner cannot be null");
        }

        String learnerId = learner.learnerId();
        if (enrolledLearners.containsKey(learnerId) || waitlistedLearners.containsKey(learnerId)) {
            return result(
                    learnerId,
                    RegistrationStatus.ALREADY_REGISTERED,
                    "Learner is already registered.",
                    null);
        }

        if (!learner.prerequisiteSatisfied()) {
            return result(
                    learnerId,
                    RegistrationStatus.INELIGIBLE,
                    "Learner has not satisfied the prerequisite.",
                    null
            );
        }

        if (enrolledLearners.size() < capacity) {
            enrolledLearners.put(learnerId, learner);

            return result(
                    learnerId,
                    RegistrationStatus.ENROLLED,
                    "Learner enrolled successfully.",
                    null
            );
        }
        waitlistedLearners.put(learnerId, learner);
        return result(
                learnerId,
                RegistrationStatus.WAITLISTED,
                "Course is full. Learner added to the waitlist.",
                null
        );
    }

    public synchronized RegistrationResult drop(String learnerId) {
        if (learnerId == null || learnerId.isBlank()) {
            throw new IllegalArgumentException("Learner ID cannot be blank");
        }

        if (enrolledLearners.remove(learnerId) == null) {
            return result(
                    learnerId,
                    RegistrationStatus.NOT_ENROLLED,
                    "Learner is not currently enrolled.",
                    null
            );
        }

        String promotedLearnerId = promoteNextEligibleLearner();
        return result(
                learnerId,
                RegistrationStatus.DROPPED,
                promotedLearnerId == null
                        ? "Learner dropped successfully. No eligible learner to promote."
                        : "Learner dropped successfully. Next eligible learner promoted.",
                promotedLearnerId
        );
    }

    private String promoteNextEligibleLearner() {
        for (Map.Entry<String, Learner> entry : waitlistedLearners.entrySet()) {
            Learner learner = entry.getValue();
            if (learner.prerequisiteSatisfied()) {
                String learnerId = entry.getKey();
                enrolledLearners.put(learnerId, learner);
                waitlistedLearners.remove(learnerId);
                return learnerId;
            }
        }
        return null;
    }

    public synchronized List<String> getEnrolledLearnerIds() {
        return new ArrayList<>(enrolledLearners.keySet());
    }

    public synchronized List<String> getWaitlistedLearnerIds() {
        return new ArrayList<>(waitlistedLearners.keySet());
    }

    private RegistrationResult result(String learnerId, RegistrationStatus status, String message, String promotedLearnerId) {
        return new RegistrationResult(
                learnerId,
                status,
                message,
                promotedLearnerId
        );
    }

    public synchronized void updatePrerequisiteStatus(String learnerId, boolean prerequisiteSatisfied) {
        Learner learner = waitlistedLearners.get(learnerId);
        if (learner == null) {
            throw new IllegalArgumentException("Learner is not on the waitlist.");
        }
        waitlistedLearners.put(learnerId, new Learner(learnerId, prerequisiteSatisfied));
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getAvailableSeats() {
        return capacity - enrolledLearners.size();
    }
}
