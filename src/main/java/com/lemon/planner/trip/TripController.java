package com.lemon.planner.trip;

import com.lemon.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        List<Participant> participants = this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), trip);
        TripCreateResponse response = new TripCreateResponse(trip.getId(), participants);
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

    @PatchMapping("/{id}/confirmation")
    public ResponseEntity<Trip> getConfirmation(@PathVariable UUID id) {
        Optional<Trip> trip = this.repository.findById(id);
        if(trip.isPresent()) {
            Trip tripUpdated = trip.get();
            tripUpdated.setConfirmed(true);
            this.repository.save(tripUpdated);

            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(trip.get());
        }
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> invite(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);
        if(trip.isPresent()) {
            Trip rawTrip = trip.get();
            ParticipantCreateResponse participantId = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);
            if(rawTrip.isConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }
            return ResponseEntity.ok(participantId);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getParticipants(@PathVariable UUID id) {
        List<ParticipantData> participants = this.participantService.getAllParticipantsFromEvent(id);
        return ResponseEntity.ok(participants);
    }
}
