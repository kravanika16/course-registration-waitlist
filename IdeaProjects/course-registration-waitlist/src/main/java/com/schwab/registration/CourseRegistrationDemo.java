package com.schwab.registration;

import com.schwab.registration.model.Learner;
import com.schwab.registration.model.RegistrationResult;
import com.schwab.registration.service.CourseRegistrationService;

public class CourseRegistrationDemo {

    public static void main(String[] args) {

        CourseRegistrationService registrationService = new CourseRegistrationService(2);

        System.out.println("=== Course Registration Demo ===");
        // Register learners
        registerLearner(registrationService, "L101");
        registerLearner(registrationService, "L102");
        registerLearner(registrationService, "L103");
        registerLearner(registrationService, "L104");

        // Display current registration state
        displayRegistrationState(registrationService);

        // Drop a learner and promote the next eligible learner
        System.out.println("\n=== Dropping Learner L101 ===");

        RegistrationResult dropResult = registrationService.drop("L101");
        System.out.println(dropResult.message());
        if (dropResult.promotedLearnerId() != null) {
            System.out.println("Promoted learner: " + dropResult.promotedLearnerId());
        }

        // Display final state
        displayRegistrationState(registrationService);
    }

    private static void registerLearner(CourseRegistrationService service, String learnerId) {
        RegistrationResult result = service.register(new Learner(learnerId, true));
        System.out.println(learnerId + " -> " + result.status());
    }

    private static void displayRegistrationState(CourseRegistrationService service) {
        System.out.println("\nEnrolled learners: " + service.getEnrolledLearnerIds());
        System.out.println("Waitlisted learners: " + service.getWaitlistedLearnerIds());
    }
}
