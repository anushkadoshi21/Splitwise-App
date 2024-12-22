package com.splitwise.splitwisespring.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 Total expense of every individual in a group.
 Negative number indicates perseon owed that amount by other users and vice versa
 We also have Mappings of Many to one, since an user can belong to
 multiple groups and a group has multiple users
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "groupexpense")
public class GroupExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "groupId")
    private UserGroup userGroup;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    @Column(columnDefinition = "float default 0.0")
    private float expenseAmount;

    public  void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
