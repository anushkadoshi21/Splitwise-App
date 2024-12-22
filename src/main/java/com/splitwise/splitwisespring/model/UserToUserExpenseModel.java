package com.splitwise.splitwisespring.model;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

/**
 Just a basic java class (not mapped to a table in db).
 This class is purely made for convenience of validating api query request body
 It describes in detail about an expense made by a user and who others share the expense
 This class will be modified in future for handling complex calculations of distribution of expense by percentages
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserToUserExpenseModel {
    private Long groupId;
    private String expenseName;
    private List<Long> membersParticipated;
    private Long memberPaidId;
    private float totalExpense;

}
