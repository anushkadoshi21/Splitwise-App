package com.splitwise.splitwisespring.model.ApiResponse;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary of user based on all groups
 */
@Data
@NoArgsConstructor
public class UserSummary {

    private float totalOwed;
    private float totalGets;

    public UserSummary(float totalOwed,float totalGets){
        this.totalGets=totalGets;
        this.totalOwed=totalOwed;
    }

}
