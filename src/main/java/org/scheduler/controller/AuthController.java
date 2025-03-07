package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scheduler.controller.interfaces.AuthApi;
import org.scheduler.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<Map<String, String>> authenticateByToken(String token) {
        log.info("Authentication request with one-time token");
        try {
            Map<String, String> tokens = authService.validateOneTimeTokenAndGenerateJwt(token);
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
