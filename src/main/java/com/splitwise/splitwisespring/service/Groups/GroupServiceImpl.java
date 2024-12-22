package com.splitwise.splitwisespring.service.Groups;

import com.splitwise.splitwisespring.model.ApiResponse.UserIdGroups;
import com.splitwise.splitwisespring.model.ApiResponse.UserSummary;
import com.splitwise.splitwisespring.model.GroupExpense;
import com.splitwise.splitwisespring.model.User;
import com.splitwise.splitwisespring.model.UserGroup;
import com.splitwise.splitwisespring.repository.GroupExpenseRepository;
import com.splitwise.splitwisespring.repository.GroupRepository;
import com.splitwise.splitwisespring.repository.UserRepository;
import com.splitwise.splitwisespring.repository.UserToUserExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private GroupRepository groupRepository;
    private UserToUserExpenseRepository userToUserExpenseRepository;
    private UserRepository userRepository;
    private GroupExpenseRepository groupExpenseRepository;

    public GroupServiceImpl(GroupRepository groupRepository,
                        UserRepository userRepository,
                            GroupExpenseRepository groupExpenseRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupExpenseRepository = groupExpenseRepository;
    }

    /**
     * Method to create a new group, groupId is assigned automatically
     * Only groupName and groupmembersIdto be provided
     * For each user in group it creates an mapping and new record in GroupExpense table
     * which provides insights on overall amout due of a user in that group
     * @param userGroup
     * @return
     */
    @Override
    @Transactional
    public Long createGroup(UserGroup userGroup)  {
        UserGroup savedGroup= groupRepository.save(userGroup);
        List<Long> userIds = userGroup.getGroupMembers();
        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            GroupExpense mapping = new GroupExpense();
            mapping.setUserGroup(savedGroup);
            mapping.setUser(user);
            groupExpenseRepository.save(mapping);
        }
        System.out.println("Saved group: " + savedGroup.getGroupId());
        return savedGroup.getGroupId();
    }

    /**
     * Method to Update the group for groupName, groupmembers - adding new members or
     * removing them
     * For the updated list of members provided, this method checks for
     * new members, adds the mapping to groupexpense table
     * and for removed members, updates group and deletes the record.
     * Additional functionality to implement is removal only possible when amoutDue
     * of that user should be 0
     * @param group
     * @param id
     * @return
     */
    @Override
    public UserGroup updateGroup(UserGroup group,Long id) {
        UserGroup initialGroup=groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
        initialGroup.setGroupName(group.getGroupName());
        List<Long> currentUserIds = initialGroup.getGroupMembers();
        List<Long> newuserIds = List.copyOf(group.getGroupMembers());
        for (Long userId : newuserIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            GroupExpense existingCombo = this.groupExpenseRepository.findByGroupIdAndUserId(id, userId);
            if (existingCombo != null) {
                System.out.println("Existing combo found for user ID " + userId);
            } else {
                GroupExpense mapping = new GroupExpense();
                mapping.setUserGroup(initialGroup);
                mapping.setUser(user);
                groupExpenseRepository.save(mapping);
            }
        }
        List <Long> diff= currentUserIds.stream()
                .filter(element -> !newuserIds.contains(element))
                .collect(Collectors.toList());
        for (Long currentUserId : diff) {
            System.out.println("Deleting user "+currentUserId+" from group "+id);
                groupExpenseRepository.deleteByGroupIdAndUserId(id, currentUserId);
        }
        initialGroup.setGroupMembers(group.getGroupMembers());
        return groupRepository.save(initialGroup);
    }

    /**
     * Deletes the group with provided groupId after checking for Id authenticity
     * @param id
     */
    @Override
    public void deleteGroup(Long id) {
        groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
        groupRepository.deleteById(id);
    }

    /**
     * Method to get all the groups in the db
     * @return
     */
    @Override
    public List<UserGroup> getAllGroups() {
        return (List<UserGroup>) groupRepository.findAll();
    }

    /**
     * Fetch group info by it's Id
     * after checking the authenticity of that Id
     * @param id
     * @return
     */
    @Override
    public UserGroup getGroupById(Long id) {
        UserGroup group= groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));;
        return group;
    }

    /**
     * Gets all groups user is part of using user Id
     * @param id
     * @return
     */
    @Override
    public List<UserIdGroups> getGroupsByUserId(Long id) {
        List<UserGroup> grps=groupRepository.findGroupsByUserId(id);
        List<UserIdGroups> loggedUserGroups= new ArrayList<>();
        for (UserGroup g: grps){
            loggedUserGroups.add(new UserIdGroups(g.getGroupName(),g.getGroupId(),g.memberCount()));
        }
        return loggedUserGroups;
}

    /**
     * Summary of user based on all groups
     * @param id
     * @return
     */
    @Override
    public UserSummary getSummary(Long id) {
        float gives= groupExpenseRepository.sumByUserId(id);
        float gets= userToUserExpenseRepository.sumByUserId(id);
        UserSummary u=new UserSummary(gives,gets);
        return u;
    }
}
