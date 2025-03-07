package org.scheduler.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for authentication tokens
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponse {
    
    @Schema(description = "JWT access token for API authorization", 
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhbDc3ZXgxIiwicm9sZXMiOltdLCJpYXQiOjE3NDEzMzU0NjcsImV4cCI6MTc0MTQyMTg2N30.e6wrXQs9Tu8LNkVgmHUZKHJSB2k5b2UdCgw4zODQXrljOvpieWI8rVNSZOJ3P5cVtFa9InOxWQLCQ2dnAFMpxA")
    private String accessToken;
    
    @Schema(description = "JWT refresh token for obtaining a new access token", 
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3NDEzMzU0NjcsImV4cCI6MTc0MTk0MDI2N30.UeVIcJUvRPoptjbNKnEeTzgxrWjgipD7LA_uIfanFTSwPXhRwCWPO33-n-fu2U_jYjX11k3i9H-8tPQ01jZ8Xw")
    private String refreshToken;
}
