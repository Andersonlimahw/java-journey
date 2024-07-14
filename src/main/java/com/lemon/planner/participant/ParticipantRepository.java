package com.lemon.planner.participant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    Optional<Object> findByTripId(UUID tripId);
}
