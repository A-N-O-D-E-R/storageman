package com.anode.storage.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.anode.storage.dto.LoginRequest;
import com.anode.storage.dto.TokenResponse;
import com.anode.storage.entity.core.User;
import com.anode.storage.repository.UserRepository;
import com.anode.storage.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final JwtService jwt;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!BCrypt.checkpw(req.getPassword(), user.getPasswordHash()))
            return ResponseEntity.status(401).body("Invalid credentials");

        return ResponseEntity.ok(new TokenResponse(jwt.generateToken(user.getEmail())));
    }
}

