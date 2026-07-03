package com.govmt.sgd.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping
    public String authenticate(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }
}
