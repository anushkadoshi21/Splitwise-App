package com.splitwise.splitwisespring.controller;

import com.splitwise.splitwisespring.model.ApiResponse.UserIdGroups;
import com.splitwise.splitwisespring.model.User;
import com.splitwise.splitwisespring.model.UserGroup;
import com.splitwise.splitwisespring.service.Groups.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Group specific actions.
 * Api Queries for Group specific CRUD operations
 */
@RestController
@RequestMapping("/groups")
@CrossOrigin(origins = "http://localhost:3000")
public class UserGroupController {

    @Autowired private GroupService groupService;

    public UserGroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Post request to create a new group.
     * Request body contains data structure of type userGroup defined in models
     * @param userGroup
     * @return
     */
    @PostMapping("/createGroup")
    public String createUser(@Valid @RequestBody UserGroup userGroup) {
        System.out.println("Incoming user: " + userGroup);
        Long groupId=groupService.createGroup(userGroup);
        return "Group Created successfully with ID: "+groupId;
    }

    /**
     * Get request for fetching all existing groups
     * @return
     */
    @GetMapping("/allGroups")
    public List<UserGroup> getAllUsers() {
        return groupService.getAllGroups();
    }

    /**
     * Get request for fetching a specific group by
     * mentioning it's groupId in pathVariable
     * @param id
     * @return
     */
    @GetMapping("/getGroup/{id}")
    public UserGroup getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id);
    }

    /**
     * PUT type api request for updating an existing group, using path variable for groupId
     * to be updated and request body having UserGroup ds
     * @param id
     * @param userGroup
     * @return
     */
    @PutMapping("/updateGroup/{id}")
    public UserGroup updateGroup(@PathVariable Long id, @Valid @RequestBody UserGroup userGroup) {
        return groupService.updateGroup(userGroup, id);
    }

    /**
     * Delete api request for deleting a group using it's groupId
     * @param id
     * @return
     */
    @DeleteMapping("/deleteGroup/{id}")
    public String deleteUser(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return "Deleted Successfully";
    }

    /**
     * Fetched groups the user is part of based on user id
     * @param id
     * @return
     */
    @GetMapping(value="/userGroups/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserIdGroups> getUserGroups(@PathVariable Long id) {
        return groupService.getGroupsByUserId(id);
    }


}
