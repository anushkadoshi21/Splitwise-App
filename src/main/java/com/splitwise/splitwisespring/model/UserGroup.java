package com.splitwise.splitwisespring.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

/**
 Basic group entity consisting of user(s) which also defines groups table in db schema
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "groups")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long groupId;
    private String groupName;
    @ElementCollection
    private List<Long> groupMembers;

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int memberCount(){
        return groupMembers.size();
    }

}
