package com.splitwise.splitwisespring.service.Expense;

import com.splitwise.splitwisespring.model.ApiResponse.GroupSummary;
import com.splitwise.splitwisespring.model.ApiResponse.OweList;
import com.splitwise.splitwisespring.model.GroupMemberExpense;
import com.splitwise.splitwisespring.model.UserToUserExpenseModel;
import com.splitwise.splitwisespring.model.UsertoUserExpense;

import java.util.List;

public interface ExpenseService {

    /**
     * Adds expenseto a group
     * @param userToUserExpenseModel
     * @return
     */
    String addExpense(UserToUserExpenseModel userToUserExpenseModel);

    void deleteExpense(Long id);

    /**
     * Settles all expenses for a particular group with a particular user
     * @param groupId
     * @param oweId
     */
    void settleExpense(Long groupId, Long oweId);

    /**
     * Internal function call implemented for database update of user to user split in a group
     * @param userToUserExpenseModel
     * @return
     */
    Long createExpenseGroup(UserToUserExpenseModel userToUserExpenseModel);

    /**
     * Gets all expenses from group mentioned
     * @param id
     * @return
     */
    List<GroupMemberExpense> getExpensesForGroup(Long id);

    /**
     * Returns expenses for a owe list for a user
     * @param id
     * @return
     */
    List <OweList> getExpensesForOweList(Long id);

    /**
     * Gets all the transactions to be done/unsettled for a group
     * @param id
     * @return
     */
    List <GroupSummary> getGroupSummary(Long id);
}
