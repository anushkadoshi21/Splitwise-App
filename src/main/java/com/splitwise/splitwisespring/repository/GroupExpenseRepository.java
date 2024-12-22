package com.splitwise.splitwisespring.repository;

import com.splitwise.splitwisespring.model.GroupExpense;
import com.splitwise.splitwisespring.model.UserGroup;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface GroupExpenseRepository extends JpaRepository<GroupExpense, Long> {
        /**
         * Method to find GroupExpense using groupId and UserId
         * @param groupId
         * @param userId
         * @return
         */
        @Query(value="Select * from GroupExpense g  where g.group_Id= :groupId and g.user_Id= :userId", nativeQuery = true)
        GroupExpense findByGroupIdAndUserId(Long groupId, Long userId);

        /**
         * Method to delete groupexpense by groupId and userId
         * @param groupId
         * @param userId
         */
        @Modifying
        @Query(value="DELETE  from GroupExpense g  where g.group_Id= :groupId and g.user_Id= :userId", nativeQuery = true)
        void deleteByGroupIdAndUserId(Long groupId, Long userId);

        /**
         * Update GroupExpense when new expense is added or settled
         * @param amt
         * @param groupId
         * @param userId
         */
        @Modifying
        @Query(value="update GroupExpense g set g.expense_amount= :amt where g.group_Id= :groupId and g.user_Id= :userId",nativeQuery = true)
        void updateBYGroupIdAndUserId(float amt,Long groupId, Long userId);

        /**
         * Calculates total expense / money spent by user
         * @param user_Id
         * @return
         */
        @Query(value="select coalesce(SUM(g.expense_amount),0.0) from GroupExpense g where g.user_Id= :user_Id",nativeQuery = true)
        float sumByUserId(long user_Id);

}
