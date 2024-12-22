package com.splitwise.splitwisespring.controller;


import com.splitwise.splitwisespring.model.ApiResponse.GroupSummary;
import com.splitwise.splitwisespring.model.ApiResponse.OweList;
import com.splitwise.splitwisespring.model.GroupMemberExpense;
import com.splitwise.splitwisespring.model.UserToUserExpenseModel;
import com.splitwise.splitwisespring.service.Expense.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 Controller for Expense specific actions.
 * Api Queries for Expensespecific CRUD operations
 */
@RestController
@RequestMapping("/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;


    /**
     * Post reuest for creating a new expense while
     * leveraging userTouserExpenseModel class to validate request body
     * @param userToUserExpenseModel
     * @return
     */
    @PostMapping("/createExpense")
    public String createExpense(@Valid @RequestBody UserToUserExpenseModel userToUserExpenseModel) {
        String expenseName=expenseService.addExpense(userToUserExpenseModel);
        return "Expense Created Successfully with Id "+expenseName;
    }

    /**
     * Settles expenses of loggedIn user with a particular group id
     * @param groupId
     * @param oweId
     * @return
     */
    @PostMapping("/settleExpense/{groupId}/{oweId}")
    public String settleExpense(@PathVariable("groupId") Long groupId, @PathVariable("oweId") Long oweId){
        expenseService.settleExpense(groupId,oweId);
        return "Transaction for group id :"+groupId+" and owe Id: "+oweId+" Settled successfully";
    }

    /**
     * Gets group expenses with all the details of who paid, memebers participated
     * @param id
     * @return
     */
    @GetMapping("/getGroupExpenses/{id}")
    public List<GroupMemberExpense> getGroupExpenses(@PathVariable("id") Long id){
        return expenseService.getExpensesForGroup(id);
    }

    /**
     * Returns unsettled expenses summary of users and expenses of a particular group
     * @param id
     * @return
     */
    @GetMapping("/getOweLists/{id}")
    public List<OweList> getOweLists(@PathVariable("id") Long id){
        return expenseService.getExpensesForOweList(id);
    }

    /**
     * Fetches total expense of all users individually for groufor visualization purposes
     * @param id
     * @return
     */
    @GetMapping(value = "/getGroupSummary/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getGroupSummary(@PathVariable Long id){
        List<GroupSummary> lis=expenseService.getGroupSummary(id);
        return ResponseEntity.ok(lis);
    }

}
