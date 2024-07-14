package com.lemon.planner.trip;

import com.lemon.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping()
    public ResponseEntity<List<Trip>> list() {
        List<Trip> trips = this.repository.findAll();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getById(@PathVariable UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> update(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);
        if(trip.isPresent()) {
            Trip tripToUpdate = trip.get();
            tripToUpdate.setEndsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            tripToUpdate.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            tripToUpdate.setDestination(payload.destination());
            this.repository.save(tripToUpdate);

            return ResponseEntity.ok(tripToUpdate);
        }
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}