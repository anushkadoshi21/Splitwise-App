package com.splitwise.splitwisespring.model.ApiResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Group summary of which users owes what amount
 */
@Data
@NoArgsConstructor
public class GroupSummary {
    Long userId;
    float amount;

    public GroupSummary(Long idd, float f) {
        userId = idd;
        amount = f;
    }
}
