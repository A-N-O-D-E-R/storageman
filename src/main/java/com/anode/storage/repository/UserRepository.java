package com.anode.storage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
