package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scheduler.controller.interfaces.AuthApi;
import org.scheduler.dto.response.AuthTokenResponse;
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
    public ResponseEntity<AuthTokenResponse> authenticateByToken(String token) {
        log.info("Authentication request with one-time token: {}", token);
        Map<String, String> tokens = authService.validateOneTimeTokenAndGenerateJwt(token);
        
        AuthTokenResponse response = new AuthTokenResponse(
            tokens.get("accessToken"),
            tokens.get("refreshToken")
        );
        
        return ResponseEntity.ok(response);
    }
}
