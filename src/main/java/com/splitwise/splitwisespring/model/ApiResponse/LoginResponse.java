package com.splitwise.splitwisespring.model.ApiResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic login response for frontend
 */
@Data
@NoArgsConstructor
public class LoginResponse {
    private Long userID;
    private String message;

    public LoginResponse(Long userID, String message) {
        this.userID = userID;
        this.message = message;
    }
}
