package com.lemon.planner.trip;

import com.lemon.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")

public class TripController {
    @Autowired
    private TripRepository repository;

    @Autowired
    private ParticipantService participantService;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {

        Trip trip = new Trip(payload);
        this.repository.save(trip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), trip);
        TripCreateResponse response = new TripCreateResponse(trip.getId());
        return ResponseEntity.ok(response);
    }
}
