package com.schwab.registration.controller;

import com.schwab.registration.model.Learner;
import com.schwab.registration.model.RegistrationResult;
import com.schwab.registration.service.CourseRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final CourseRegistrationService registrationService;

    public RegistrationController(
            CourseRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // View course registration details
    @GetMapping
    public CourseRegistrationResponse getRegistrations() {
        return new CourseRegistrationResponse(
                registrationService.getCapacity(),
                registrationService.getAvailableSeats(),
                registrationService.getEnrolledLearnerIds(),
                registrationService.getWaitlistedLearnerIds()
        );
    }

    // Register a learner
    @PostMapping
    public RegistrationResult registerLearner(
            @RequestBody RegistrationRequest request) {

        if (request == null || request.learnerId() == null
                || request.learnerId().isBlank()) {
            throw new IllegalArgumentException(
                    "Learner ID cannot be blank");
        }

        Learner learner = new Learner(
                request.learnerId(),
                request.prerequisiteSatisfied()
        );

        return registrationService.register(learner);
    }

    // Drop an enrolled learner
    @DeleteMapping("/{learnerId}")
    public RegistrationResult dropLearner(
            @PathVariable String learnerId) {

        return registrationService.drop(learnerId);
    }

    // Handle invalid requests
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidRequest(
            IllegalArgumentException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    // Request received from the browser
    public record RegistrationRequest(
            String learnerId,
            boolean prerequisiteSatisfied) {
    }

    // Registration information returned to the browser
    public record CourseRegistrationResponse(
            int capacity,
            int availableSeats,
            List<String> enrolledLearners,
            List<String> waitlistedLearners) {
    }
}
