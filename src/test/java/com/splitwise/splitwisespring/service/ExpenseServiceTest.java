package com.splitwise.splitwisespring.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.splitwise.splitwisespring.model.*;
import com.splitwise.splitwisespring.repository.*;
import com.splitwise.splitwisespring.service.Expense.ExpenseService;
import com.splitwise.splitwisespring.service.Expense.ExpenseServiceImpl;
import com.splitwise.splitwisespring.service.Users.UserService;
import com.splitwise.splitwisespring.service.Users.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



public class ExpenseServiceTest {

    @Mock
    private GroupExpenseRepository groupExpenseRepository;
    @Mock
    private UserToUserExpenseRepository userToUserExpenseRepository;

    @Mock GroupMemberExpenseRepository groupMemberExpenseRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testAddExpenseWhenGroupNotFound() {
        Long groupId = 1L;
        Long memberOwedId = 100L;
        List<Long> memberIds = Arrays.asList(100L, 101L);
        Float expense= 100.0f;

        UserToUserExpenseModel expenseModel = new UserToUserExpenseModel(groupId,"Trip",memberIds,memberOwedId,expense);

        given(groupRepository.findById(groupId)).willReturn(Optional.empty());

        Exception exception= assertThrows(RuntimeException.class, () -> {
            expenseService.addExpense(expenseModel);
        });
        assertThat(exception.getMessage()).isEqualTo("Group not found 1");
    }


    @Test
    public void testAddExpenseWhenUserNotFound() {
        Long groupId = 1L;
        Long memberOwedId = 100L;
        List<Long> memberIds = Arrays.asList(100L, 101L);
        Float expense= 100.0f;

        UserToUserExpenseModel expenseModel = new UserToUserExpenseModel(groupId,"Picnic",memberIds,memberOwedId,expense);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new UserGroup()));
        when(userRepository.findById(memberOwedId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseService.addExpense(expenseModel);
        });
        assertThat(exception.getMessage()).isEqualTo("User not found: 100");
    }

    @Test
    public void testAddExpenseWhenNewExpenseRecord() {
        // Arrange
        Long groupId = 1L;
        Long memberOwedId = 100L;
        List<Long> memberIds = Arrays.asList(100L, 101L, 102L);
        float totalExpense = 300f;
        float dividedExpense = totalExpense / memberIds.size();

        UserGroup group = new UserGroup();
        group.setGroupId(groupId);
        User payer = new User(memberOwedId,"DummyUser","123");

        UserToUserExpenseModel expenseModel = new UserToUserExpenseModel(groupId,"LunchTrip",memberIds,memberOwedId,dividedExpense);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(memberOwedId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        String result = expenseService.addExpense(expenseModel);

        assertThat(result).isEqualTo("Lunch");
        verify(userToUserExpenseRepository, times(3)).save(any(UsertoUserExpense.class));
    }

    @Test
    void deleteExpenseTest(){
        Long expenseId = 1L;
        expenseService.deleteExpense(expenseId);

        verify(userToUserExpenseRepository, times(1)).deleteById(expenseId);
    }

    @Test
    void SettleExpenseSuccessTest() {
        Long groupId = 1L;
        Long oweId = 2L;
        List<Long> owedIds = List.of(3L, 4L);
        GroupMemberExpense expense1 = new GroupMemberExpense(1L, groupId, 3L, "[2]", false);
        GroupMemberExpense expense2 = new GroupMemberExpense(2L, groupId, 4L, "[2]", false);
        when(userToUserExpenseRepository.getUserOwedIdByGroupIdAndUserOweId(groupId, oweId)).thenReturn(owedIds);
        when(groupMemberExpenseRepository.findByOwedIdAndGroupId(groupId, 3L)).thenReturn(List.of(expense1));
        when(groupMemberExpenseRepository.findByOwedIdAndGroupId(groupId, 4L)).thenReturn(List.of(expense2));
        expenseService.settleExpense(groupId, oweId);
        verify(userToUserExpenseRepository, times(1)).deleteByGroupIdAndUserAndUserOweId(groupId, oweId);
        verify(groupExpenseRepository, times(1)).updateBYGroupIdAndUserId(0.0f, groupId, oweId);
        verify(groupMemberExpenseRepository, times(1)).updateSettleStatus(eq(1L), eq("[]"), eq(true));
        verify(groupMemberExpenseRepository, times(1)).updateSettleStatus(eq(2L), eq("[]"), eq(true));
    }

    @Test
    void GetExpensesForGroupSuccessTest() {
        Long groupId = 1L;
        GroupMemberExpense expense1 = new GroupMemberExpense(1L, groupId, "Dinner", "[2, 3]", "[3]",2L,  100.0f, false);
        GroupMemberExpense expense2 = new GroupMemberExpense(2L, groupId, "Lunch", "[2, 4]", "[4]",2L,  200.0f, false);
        List<GroupMemberExpense> expectedExpenses = List.of(expense1, expense2);

        when(groupMemberExpenseRepository.findByGroupId(groupId)).thenReturn(expectedExpenses);

        List<GroupMemberExpense> actualExpenses = expenseService.getExpensesForGroup(groupId);

        assertThat(actualExpenses.equals(expectedExpenses));
        verify(groupMemberExpenseRepository, times(1)).findByGroupId(groupId);
    }

}
