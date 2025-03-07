package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.dto.response.AuthTokenResponse;
import org.scheduler.interceptor.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Authentication", description = "Operations related to authentication")
@RequestMapping("/api/auth")
public interface AuthApi {

    @GetMapping("/token")
    @Operation(
        summary = "Authenticate with one-time token",
        description = "Validates a one-time token and generates JWT tokens for authentication"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Authentication successful",
        content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))
    )
    @ApiResponse(
        responseCode = "401", 
        description = "Authentication failed",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
        responseCode = "500", 
        description = "Server error",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<AuthTokenResponse> authenticateByToken(
        @Parameter(description = "One-time authentication token") @RequestParam String token
    );
}
