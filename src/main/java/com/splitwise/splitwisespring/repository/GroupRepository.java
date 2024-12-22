package com.splitwise.splitwisespring.repository;

import com.splitwise.splitwisespring.model.UserGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GroupRepository extends CrudRepository<UserGroup, Long> {
    /**
     * Finds all group user is part of
     */
    @Query("SELECT g FROM UserGroup g WHERE :userId MEMBER OF g.groupMembers")
    List<UserGroup> findGroupsByUserId(Long userId);
}
