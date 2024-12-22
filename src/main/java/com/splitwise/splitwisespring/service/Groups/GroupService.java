package com.splitwise.splitwisespring.service.Groups;

import com.splitwise.splitwisespring.model.ApiResponse.UserIdGroups;
import com.splitwise.splitwisespring.model.ApiResponse.UserSummary;
import com.splitwise.splitwisespring.model.UserGroup;

import java.util.List;

public interface GroupService {

    /**
     * Method to create a new group, groupId is assigned automatically
     *      * Only groupName and groupmembersIdto be provided
     *      * For each user in group it creates an mapping and new record in GroupExpense table
     *      * which provides insights on overall amout due of a user in that group
     * @param group
     * @return
     */
    Long createGroup(UserGroup group);

    /**
     * Method to Update the group for groupName, groupmembers - adding new members or
     *      * removing them
     *      * For the updated list of members provided, this method checks for
     *      * new members, adds the mapping to groupexpense table
     *      * and for removed members, updates group and deletes the record.
     *      * Additional functionality to implement is removal only possible when amoutDue
     *      * of that user should be 0
     * @param group
     * @param id
     * @return
     */
    UserGroup updateGroup(UserGroup group,Long id);

    /**
     * Deletes the group with provided groupId after checking for Id authenticity
     * @param id
     */
    void deleteGroup(Long id);

    /**
     * Method to get all the groups in the db
     * @return
     */
    List<UserGroup> getAllGroups();

    /**
     * Fetch group info by it's Id
     * after checking the authenticity of that Id
     * @param id
     * @return
     */
    UserGroup getGroupById(Long id);

    /**
     * Gets all groups user is part of using user Id
     * @param id
     * @return
     */
    List<UserIdGroups> getGroupsByUserId(Long id);

    /**
     * Summary of user based on all groups
     * @param id
     * @return
     */
    UserSummary getSummary(Long id);
}
