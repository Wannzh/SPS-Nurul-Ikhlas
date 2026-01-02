package com.sps.nurul_ikhlas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.payload.JwtResponse;
import com.sps.nurul_ikhlas.payload.LoginRequest;
import com.sps.nurul_ikhlas.payload.RegisterRequest;
import com.sps.nurul_ikhlas.payload.RegisterResponse;
import com.sps.nurul_ikhlas.security.JwtUtils;
import com.sps.nurul_ikhlas.security.UserDetailsImpl;
import com.sps.nurul_ikhlas.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final JwtUtils jwtUtils;
        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(loginRequest.getUsername());

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                String role = userDetails.getAuthorities().stream()
                                .findFirst()
                                .map(item -> item.getAuthority().replace("ROLE_", ""))
                                .orElse("");

                JwtResponse response = JwtResponse.builder()
                                .token(jwt)
                                .type("Bearer")
                                .username(userDetails.getUsername())
                                .role(role)
                                .build();

                return ResponseEntity.ok(response);
        }

        @PostMapping("/register")
        public ResponseEntity<RegisterResponse> registerStudent(@Valid @RequestBody RegisterRequest request) {
                RegisterResponse response = authService.registerStudent(request);
                return ResponseEntity.ok(response);
        }
}
