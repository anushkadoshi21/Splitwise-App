package com.splitwise.splitwisespring.repository;

import com.splitwise.splitwisespring.model.GroupExpense;
import com.splitwise.splitwisespring.model.GroupMemberExpense;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Transactional
public interface GroupMemberExpenseRepository extends JpaRepository<GroupMemberExpense, Long> {

    /**
     * Basic query to find group expenses using unique id
     * @param groupId
     * @return
     */
    @Query(value="Select * from  GroupMemberExpense g  where g.group_Id= :groupId", nativeQuery = true)
    List<GroupMemberExpense> findByGroupId(Long groupId);

    /**
     * Finds expes by group Id and Owed userId
     * @param groupId
     * @param owedId
     * @return
     */
    @Query(value="Select * from GroupMemberExpense g where g.group_Id= :groupId and g.settle_Status= false and g.member_Paid_Id= :owedId",nativeQuery = true)
    List<GroupMemberExpense> findByOwedIdAndGroupId(Long groupId, Long owedId);

    /**
     * Once every memeber participated in a particular expense has paid the contribution, the expense is settled
     * @param id
     * @param memberUnpaid
     * @param status
     */
    @Modifying
    @Query(value="Update GroupMemberExpense g set g.settle_Status= :status, g.members_Un_Paid= :memberUnpaid where g.id= :id " ,nativeQuery = true)
    void updateSettleStatus(Long id, String memberUnpaid,boolean status);
}
