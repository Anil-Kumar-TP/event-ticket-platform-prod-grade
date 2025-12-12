package com.anil.event_ticket.repository;

import com.anil.event_ticket.domain.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User,@NonNull UUID> {

    Optional<User> findByEmail(@NonNull String email);
}