package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "Authentication", description = "Operations related to authentication")
@RequestMapping("/api/auth")
public interface AuthApi {

    @GetMapping("/token")
    @Operation(
        summary = "Authenticate with one-time token",
        description = "Validates a one-time token and generates JWT tokens for authentication"
    )
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    ResponseEntity<Map<String, String>> authenticateByToken(
        @Parameter(description = "One-time authentication token") @RequestParam String token
    );
}
