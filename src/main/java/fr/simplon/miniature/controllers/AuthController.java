package fr.simplon.miniature.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import fr.simplon.miniature.dto.JwtAuthenticationResponse;
import fr.simplon.miniature.dto.LoginRequest;
import fr.simplon.miniature.models.User;
import fr.simplon.miniature.repositories.UserRepository;
import fr.simplon.miniature.security.JwtTokenProvider;
import jakarta.persistence.EntityExistsException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        // Generate JWT based on the username
        String jwt = tokenProvider.generateToken(authentication.getName());
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
    
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(@RequestBody LoginRequest loginRequest) {
        boolean exist = userRepository.findByUsername(loginRequest.getUsername()).isPresent();
        
        if(exist) {
            throw new EntityExistsException("Account with this username already exist in database.");
        }

        userRepository.saveAndFlush(User.builder()
            .username(loginRequest.getUsername())
            .password(passwordEncoder.encode(loginRequest.getPassword()))
            .build()
        );
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        // Generate JWT based on the username
        String jwt = tokenProvider.generateToken(authentication.getName());
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
    
}
