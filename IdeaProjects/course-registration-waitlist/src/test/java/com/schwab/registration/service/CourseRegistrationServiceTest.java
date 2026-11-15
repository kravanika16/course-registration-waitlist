
package com.schwab.registration.service;

import com.schwab.registration.model.Learner;
import com.schwab.registration.model.RegistrationResult;
import com.schwab.registration.model.RegistrationStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseRegistrationServiceTest {

    private CourseRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new CourseRegistrationService(2);
    }

    @Test
    void shouldEnrollLearnerWhenSeatsAreAvailable() {
        RegistrationResult result =
                registrationService.register(
                        new Learner("L101", true)
                );

        assertEquals(RegistrationStatus.ENROLLED, result.status());
        assertEquals(
                1,
                registrationService.getEnrolledLearnerIds().size()
        );
    }

    @Test
    void shouldAddLearnerToWaitlistWhenCourseIsFull() {
        registrationService.register(new Learner("L101", true));
        registrationService.register(new Learner("L102", true));

        RegistrationResult result =
                registrationService.register(
                        new Learner("L103", true)
                );

        assertEquals(RegistrationStatus.WAITLISTED, result.status());
        assertEquals(
                "L103",
                registrationService.getWaitlistedLearnerIds().get(0)
        );
    }

    @Test
    void shouldNotRegisterSameLearnerTwice() {
        registrationService.register(new Learner("L101", true));

        RegistrationResult result =
                registrationService.register(
                        new Learner("L101", true)
                );

        assertEquals(
                RegistrationStatus.ALREADY_REGISTERED,
                result.status()
        );
        assertEquals(
                1,
                registrationService.getEnrolledLearnerIds().size()
        );
    }

    @Test
    void shouldPromoteEarliestEligibleWaitlistedLearnerWhenSeatOpens() {
        registrationService.register(new Learner("L101", true));
        registrationService.register(new Learner("L102", true));
        registrationService.register(new Learner("L103", true));
        registrationService.register(new Learner("L104", true));

        RegistrationResult result =
                registrationService.drop("L101");

        assertEquals(RegistrationStatus.DROPPED, result.status());
        assertEquals("L103", result.promotedLearnerId());
        assertEquals(
                java.util.List.of("L102", "L103"),
                registrationService.getEnrolledLearnerIds()
        );
        assertEquals(
                java.util.List.of("L104"),
                registrationService.getWaitlistedLearnerIds()
        );
    }

    @Test
    void shouldNotEnrollLearnerWhoHasNotSatisfiedPrerequisite() {
        RegistrationResult result =
                registrationService.register(
                        new Learner("L105", false)
                );

        assertEquals(RegistrationStatus.INELIGIBLE, result.status());
        assertEquals(
                0,
                registrationService.getEnrolledLearnerIds().size()
        );
    }

    @Test
    void shouldNotAddSameLearnerToWaitlistTwice() {
        registrationService.register(new Learner("L101", true));
        registrationService.register(new Learner("L102", true));
        registrationService.register(new Learner("L103", true));

        RegistrationResult result =
                registrationService.register(new Learner("L103", true));

        assertEquals(
                RegistrationStatus.ALREADY_REGISTERED,
                result.status()
        );

        assertEquals(
                1,
                registrationService.getWaitlistedLearnerIds().size()
        );
    }

    @Test
    void shouldSkipIneligibleLearnerAndPromoteNextEligibleLearner() {
        registrationService.register(new Learner("L101", true));
        registrationService.register(new Learner("L102", true));
        registrationService.register(new Learner("L103", true));
        registrationService.register(new Learner("L104", true));

        registrationService.updatePrerequisiteStatus("L103", false);

        RegistrationResult result =
                registrationService.drop("L101");

        assertEquals("L104", result.promotedLearnerId());

        assertEquals(
                java.util.List.of("L102", "L104"),
                registrationService.getEnrolledLearnerIds()
        );

        assertEquals(
                java.util.List.of("L103"),
                registrationService.getWaitlistedLearnerIds()
        );
    }

    @Test
    void shouldNotPromoteLearnerWhenDroppingWaitlistedLearner() {
        registrationService.register(new Learner("L101", true));
        registrationService.register(new Learner("L102", true));
        registrationService.register(new Learner("L103", true));

        RegistrationResult result =
                registrationService.drop("L103");

        assertEquals(
                RegistrationStatus.NOT_ENROLLED,
                result.status()
        );

        assertEquals(
                java.util.List.of("L103"),
                registrationService.getWaitlistedLearnerIds()
        );
    }

    @Test
    void shouldNotChangeStateWhenDroppingSameLearnerTwice() {
        registrationService.register(new Learner("L101", true));
        registrationService.register(new Learner("L102", true));
        registrationService.register(new Learner("L103", true));

        registrationService.drop("L101");

        RegistrationResult secondDrop =
                registrationService.drop("L101");

        assertEquals(
                RegistrationStatus.NOT_ENROLLED,
                secondDrop.status()
        );

        assertEquals(
                java.util.List.of("L102", "L103"),
                registrationService.getEnrolledLearnerIds()
        );
    }
}
