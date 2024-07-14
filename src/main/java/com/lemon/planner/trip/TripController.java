package com.lemon.planner.trip;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")

public class TripController {
    @PostMapping
    public ResponseEntity<String> createTrip(@RequestBody TripRequestPayload payload) {

        Trip trip = new Trip(payload);

        return ResponseEntity.ok("Trip created");
    }
}
