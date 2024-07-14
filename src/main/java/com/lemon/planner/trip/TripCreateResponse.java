package com.lemon.planner.trip;

import com.lemon.planner.participant.Participant;

import java.util.List;
import java.util.UUID;

public record TripCreateResponse(UUID uuid, List<Participant> participants) {
}
