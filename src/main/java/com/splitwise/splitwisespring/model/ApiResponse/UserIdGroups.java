package com.splitwise.splitwisespring.model.ApiResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for displaying overall group information
 */
@Data
@NoArgsConstructor
public class UserIdGroups {
    String GroupName;
    Long GroupId;
    int MemberCount;

    public UserIdGroups(String GroupName, Long GroupId, int MemberCount) {
        this.GroupName = GroupName;
        this.GroupId = GroupId;
        this.MemberCount = MemberCount;
    }
}
