package com.example.aicodereview.controller;

import com.example.aicodereview.model.User;
import com.example.aicodereview.repository.UserRepository;
import com.example.aicodereview.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        if(repository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        var user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    // DTOs
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthenticationRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthenticationResponse {
        private String token;

        public AuthenticationResponse(String token) {
            this.token = token;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
