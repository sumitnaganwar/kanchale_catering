package com.example.catering.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    private final AuthenticationManager authenticationManager;
    private final TokenService tokens;
    public AuthController(AuthenticationManager authenticationManager, TokenService tokens) { this.authenticationManager = authenticationManager; this.tokens = tokens; }
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            return Map.of("token", tokens.issue(request.email()), "role", "ADMIN");
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin email or password");
        }
    }
}
