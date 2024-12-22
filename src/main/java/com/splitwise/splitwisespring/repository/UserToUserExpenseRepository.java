package com.splitwise.splitwisespring.repository;

import com.splitwise.splitwisespring.model.UsertoUserExpense;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


@Transactional
public interface UserToUserExpenseRepository extends JpaRepository<UsertoUserExpense, Long> {

    /**
     * Check for record by grouID, userOwedId and user_OweId
     * @param groupId
     * @param userOwedId
     * @param userOweId
     * @return
     */
    @Query(value = "Select * from usertouserexpense g where g.user_Group_Id= :groupId and g.user_Owed_Id= :userOwedId and g.user_Owe_Id= :userOweId",nativeQuery = true)
    UsertoUserExpense findByGroupIdAndUserOwedIdAnduserOweId(Long groupId, Long userOwedId, Long userOweId);

    /**
     * Finds unsettled expenses based on groupId and userId
     * @param groupId
     * @return
     */
    @Query(value= "Select * from usertouserexpense g where g.user_Group_Id= :groupId and g.user_Owed_Id!=g.user_Owe_Id", nativeQuery = true)
    List<UsertoUserExpense> findByUserGroupId(Long groupId);

    /**
     * Update expense in record of a particular expenseID, expenseName and total_owed
     * @param expenseName
     * @param totalOwed
     * @param userExpenseId
     */
    @Modifying
    @Query(value = "Update usertouserexpense g set g.expense_name= :expenseName, g.total_owed= :totalOwed where g.user_expense_id= :userExpenseId",nativeQuery = true)
    void updateUsertoUserExpenseByTotalOwed(String expenseName, float totalOwed, Long userExpenseId);

    /**
     * Once the expense is settled between 2 users, delete the entries
     * @param groupId
     * @param userOweId
     */
    @Modifying
    @Query(value="Delete from usertouserexpense g where g.user_Group_Id= :groupId and g.user_Owe_Id= :userOweId ",nativeQuery = true)
    void deleteByGroupIdAndUserAndUserOweId(Long groupId, Long userOweId);

    /**
     *
     * @param groupId
     * @param userOweId
     * @return
     */
    @Query(value="Select g.user_Owed_Id from usertouserexpense g where g.user_Group_Id= :groupId and g.user_Owe_Id= :userOweId ",nativeQuery = true)
    List<Long> getUserOwedIdByGroupIdAndUserOweId(Long groupId, Long userOweId);

    /**
     * select summation of  user to user expense in a group
     * @param user_Id
     * @return
     */
    @Query(value="select coalesce(SUM(g.total_Owed),0.0) from usertouserexpense g where g.user_Owed_Id= :user_Id and g.user_Owed_Id!=g.user_Owe_Id",nativeQuery = true)
    float sumByUserId(Long user_Id);

}


