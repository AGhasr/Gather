package org.example.eventregistration.repository;

import org.example.eventregistration.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {}