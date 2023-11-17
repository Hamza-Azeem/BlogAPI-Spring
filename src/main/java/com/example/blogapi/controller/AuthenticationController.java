package com.example.blogapi.controller;


import com.example.blogapi.entity.AuthenticationResponse;
import com.example.blogapi.entity.LoginRequest;
import com.example.blogapi.entity.RegisterRequest;
import com.example.blogapi.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return new ResponseEntity<>(authenticationService.register(request), HttpStatus.CREATED);
    }
    @Transactional
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody LoginRequest request){
        return new ResponseEntity<>(authenticationService.login(request), HttpStatus.OK);
    }
}
