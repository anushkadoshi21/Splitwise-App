package com.splitwise.splitwisespring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
This entity is a table in db describing user to user money owed for a particular group.
 The purpose behind this entity is to get in detailed view of expense, who made the payment and also what
 other expenses are due to settle between concerned 2 users.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usertouserexpense")
public class UsertoUserExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserExpenseId;

    @Column
    private String expenseName;

    @Column
    private Long userGroupId;

    @Column
    private Long userOwedId;

    @Column
    private Long userOweId;

    @Column
    private float totalOwed;

}
