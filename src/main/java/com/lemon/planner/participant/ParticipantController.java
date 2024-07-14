package com.lemon.planner.participant;

import com.lemon.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController()
@RequestMapping("/participants")
public class ParticipantController {
    @Autowired
    private ParticipantRepository repository;

    @GetMapping("")
    public ResponseEntity<List<Participant>> get() {
        List<Participant> participants = this.repository.findAll();
        return ResponseEntity.ok(participants);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        Optional<Participant> participant = this.repository.findById(id);
        if(participant.isPresent()) {
            Participant participantToConfirm = participant.get();
            participantToConfirm.setIsConfirmed(true);
            participantToConfirm.setName(payload.name());

            this.repository.save(participantToConfirm);

            return ResponseEntity.ok(participantToConfirm);
        }
        return ResponseEntity.notFound().build();
    }
}
