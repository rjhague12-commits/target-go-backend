package com.targetgo.controllers;

import com.targetgo.config.JwtUtil;
import com.targetgo.dto.LoginRequest;
import com.targetgo.dto.RegisterRequest;
import com.targetgo.models.User;
import com.targetgo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;

    // ── POST /api/auth/register ───────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already registered"));
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setAddress(req.getAddress());
        user.setPhone(req.getPhone());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "name", user.getFirstName() + " " + user.getLastName()
        ));
    }

    // ── POST /api/auth/login ──────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "name", user.getFirstName() + " " + user.getLastName()
        ));
    }
}
