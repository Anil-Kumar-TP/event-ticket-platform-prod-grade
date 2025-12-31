package com.anil.event_ticket.repository;

import com.anil.event_ticket.domain.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User,@NonNull UUID> {

// Fetch roles eagerly during login to avoid additional lazy-loading queries.
// Login is the only path where we intentionally hit the database.

        @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
        Optional<User> findByEmailWithRoles(@Param("email") String email);
    }