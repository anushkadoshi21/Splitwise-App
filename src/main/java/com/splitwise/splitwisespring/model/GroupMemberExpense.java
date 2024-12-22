package com.splitwise.splitwisespring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Keeps track of groupId, specific expense, and other specific related to the expense
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "groupmemberexpense")
public class GroupMemberExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long groupId;
    private String expenseName;
    private String membersParticipated;
    private String membersUnPaid;
    private Long memberPaidId;
    private float totalExpense;
    private boolean settleStatus=false;

    public GroupMemberExpense(long l, Long groupId, long l1, String s, boolean b) {
        this.groupId = groupId;
        this.expenseName = s;
        this.membersParticipated = s;
        this.membersUnPaid = s;
        this.id = l;
        this.totalExpense = l1;
        this.settleStatus=b;
    }

    public Long getId(){
        return id;
    }

}
