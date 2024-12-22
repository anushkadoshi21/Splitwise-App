package com.splitwise.splitwisespring.service.Expense;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.splitwise.splitwisespring.model.*;
import com.splitwise.splitwisespring.model.ApiResponse.GroupSummary;
import com.splitwise.splitwisespring.model.ApiResponse.OweList;
import com.splitwise.splitwisespring.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final GroupExpenseRepository groupExpenseRepository;
    private UserToUserExpenseRepository userToUserExpenseRepository;
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private GroupMemberExpenseRepository groupMemberExpenseRepository;

    public ExpenseServiceImpl(UserToUserExpenseRepository userToUserExpenseRepository, GroupRepository groupRepository, UserRepository userRepository, GroupExpenseRepository groupExpenseRepository, GroupMemberExpenseRepository groupMemberExpenseRepository) {
        this.userToUserExpenseRepository = userToUserExpenseRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupExpenseRepository = groupExpenseRepository;
        this.groupMemberExpenseRepository = groupMemberExpenseRepository;
    }

    /**
     * Adds expenseto a group
     * @param expenseModel
     * @return
     */
    @Override
    @Transactional
    public Long createExpenseGroup(UserToUserExpenseModel expenseModel)  {
        GroupMemberExpense expense = GroupMemberExpense.builder()
                .groupId(expenseModel.getGroupId())
                .expenseName(expenseModel.getExpenseName())
                .membersParticipated(expenseModel.getMembersParticipated().toString())
                .memberPaidId(expenseModel.getMemberPaidId())
                .membersUnPaid(expenseModel.getMembersParticipated().toString())
                .totalExpense(expenseModel.getTotalExpense())
                .build();
        if (expenseModel.getMembersParticipated().size()==1 && expenseModel.getMembersParticipated().contains(expenseModel.getMemberPaidId())){
            expense.setSettleStatus(true);
        }
        List <Long> unpaid=expenseModel.getMembersParticipated() ;
        if (unpaid.contains(expenseModel.getMemberPaidId())){
            unpaid.remove(expenseModel.getMemberPaidId());
        }
        expense.setMembersUnPaid(unpaid.toString());
        GroupMemberExpense exp= groupMemberExpenseRepository.save(expense);
        System.out.println("Saved Expense in table recently created: " + exp);
        return exp.getGroupId();
    }

    @Override
    public List<GroupMemberExpense> getExpensesForGroup(Long id) {

    return groupMemberExpenseRepository.findByGroupId(id);
    }

    /**
     * Gets all the transactions to be done/unsettled for a group
     * @param id
     * @return
     */
    @Override
    public List<GroupSummary> getGroupSummary(Long id){
        List<GroupSummary> grpSum=new ArrayList<>();
        float f= 0.0F;
        List<GroupMemberExpense> grp=this.getExpensesForGroup(id);
        Map<Long,Float> dic=new HashMap<>();
        Gson gson=new Gson();
        for (GroupMemberExpense exp:grp){
            JsonArray j= gson.fromJson(exp.getMembersParticipated(),JsonArray.class);
            List<Long> ll=new ArrayList<>();
            for (int i=0;i<j.size();i++){
                ll.add(j.get(i).getAsLong());
            }
            float amt=exp.getTotalExpense()/j.size();
            for (Long idd:ll){
                dic.put(idd,dic.getOrDefault(idd,f)+amt);
            }
        }
        for (Map.Entry<Long,Float> entry:dic.entrySet()){
            Long idd=entry.getKey();
            f=entry.getValue();
            grpSum.add(new GroupSummary(idd,f));
        }
        return grpSum;
    }


    /**
     * Gets all expenses from group mentioned
     * @param id
     * @return
     */
    @Override
    public List<OweList> getExpensesForOweList(Long id) {
        List<UsertoUserExpense> lis=userToUserExpenseRepository.findByUserGroupId(id);
        List<OweList> oweLists=new ArrayList<>();
        for (UsertoUserExpense item:lis){
            OweList owe=new OweList(item.getUserOweId(),item.getUserOwedId(),item.getTotalOwed());
            oweLists.add(owe);
        }
        return oweLists;
    }

    /**
     * Method to add expense/ a transaction in group
     * It requires who paid the amout, who all members participated in that expense,
     * expenseName, groupId
     * If the member who paid and the member who owes that amout already has a pending setlling, the amount gets
     * added to that record with all the unsettled expenses.
     * If not, then a new record is inserted for those 2 members.
     * This happens with all the members who participated in that expense
     * Future iterations will have intelligient detection and analysis of if memberPaid and
     * member owed is same i.e if the person paid for himself and can be used for monthly analysis.
     * This method also checks for userId & groupID autheticity
     * @param userToUserExpenseModel
     * @return
     */


    @Override
    @Transactional
    public String addExpense(UserToUserExpenseModel userToUserExpenseModel) {
         System.out.println(userToUserExpenseModel);
        int flag=0;
        List<Long> memberIds= userToUserExpenseModel.getMembersParticipated();
        float dividedExpense=userToUserExpenseModel.getTotalExpense()/ memberIds.size();

        Long memberPaidId=userToUserExpenseModel.getMemberPaidId();
        UserGroup group=groupRepository.findById(userToUserExpenseModel.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found "+userToUserExpenseModel.getGroupId()));
        User memberPaid=userRepository.findById(memberPaidId)
                .orElseThrow(() -> new RuntimeException("User not found: "+memberPaidId));

        for (Long userId:memberIds) {
            UsertoUserExpense u=new UsertoUserExpense();
            u.setExpenseName(userToUserExpenseModel.getExpenseName());
            u.setUserGroupId(userToUserExpenseModel.getGroupId());
            u.setTotalOwed(dividedExpense);
            u.setUserOwedId(memberPaidId);
//            if (userId.equals(memberPaidId)){
//                u.setSettleStatus(true);
//            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found "+userId));
            u.setUserOweId(userId);
            UsertoUserExpense existingCombo= this.userToUserExpenseRepository.findByGroupIdAndUserOwedIdAnduserOweId(userToUserExpenseModel.getGroupId(), memberPaidId,userId);
            if (existingCombo!=null){
                System.out.println("Already existing combo, Add this expense in this row");
                String existingExpenseName= existingCombo.getExpenseName()+", "+userToUserExpenseModel.getExpenseName();
                float existingTotalOwed= dividedExpense+existingCombo.getTotalOwed();
                userToUserExpenseRepository.updateUsertoUserExpenseByTotalOwed(existingExpenseName,existingTotalOwed,existingCombo.getUserExpenseId());
                System.out.println("Record updated!");
                //update query
            }
            else{
                this.userToUserExpenseRepository.save(u);
            }
//            float amt_owed=userToUserExpenseRepository.sumTotalOwedByGroupId(userToUserExpenseModel.getGroupId(),userId);
//            float amt_owe=userToUserExpenseRepository.sumTotalOweByGroupId(userToUserExpenseModel.getGroupId(),userId);
            if (memberPaidId!=userId){
                float amt= groupExpenseRepository.findByGroupIdAndUserId(userToUserExpenseModel.getGroupId(),userId).getExpenseAmount();
                groupExpenseRepository.updateBYGroupIdAndUserId(amt+dividedExpense,userToUserExpenseModel.getGroupId(),userId);
            }
           else{
               flag=1;
            }
        }
        float amt= userToUserExpenseModel.getTotalExpense();
        if (flag==1){
            amt-=dividedExpense;
        }
//        float act_amt=groupExpenseRepository.findByGroupIdAndUserId(userToUserExpenseModel.getGroupId(),memberPaidId).getExpenseAmount();
//        groupExpenseRepository.updateBYGroupIdAndUserId(-amt+act_amt,userToUserExpenseModel.getGroupId(),memberPaidId);
        createExpenseGroup(userToUserExpenseModel);
        return userToUserExpenseModel.getExpenseName();
    }


    /**
     * Deletes expense with particular expense id
     * @param id
     */
    @Override
    public void deleteExpense(Long id) {
        userToUserExpenseRepository.deleteById(id);
    }

    /**
     * Settles all expenses for a particular group with a particular user
     * @param groupId
     * @param oweId
     */
    @Override
    public void settleExpense(Long groupId, Long oweId) {
        List<Long> owedIds= userToUserExpenseRepository.getUserOwedIdByGroupIdAndUserOweId(groupId,oweId);
        userToUserExpenseRepository.deleteByGroupIdAndUserAndUserOweId(groupId,oweId);
        groupExpenseRepository.updateBYGroupIdAndUserId(0.0f,groupId,oweId);
        for (long owedId:owedIds){
            List<GroupMemberExpense> g= groupMemberExpenseRepository.findByOwedIdAndGroupId(groupId,owedId);
            for (GroupMemberExpense individual: g){
                boolean status=false;
                List<Long> id = Arrays.stream(individual.getMembersUnPaid().replace("[", "").replace("]", "").split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList());
                if (id.contains(oweId)){
                    id.remove(oweId);
                    if (id.size()==0){
                        status=true;
                    }
                    groupMemberExpenseRepository.updateSettleStatus(individual.getId(), id.toString(),status);

                }
            }
        }

    }
}
