package com.splitwise.splitwisespring.controller;
import java.util.List;

import com.splitwise.splitwisespring.model.ApiRequest.LoginRequest;
import com.splitwise.splitwisespring.model.ApiResponse.*;
import com.splitwise.splitwisespring.model.User;
import com.splitwise.splitwisespring.service.Users.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user specific actions.
 * Api Queries for User specific CRUD operations
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired private UserService userService;

    /**
     * Create new user.
     * Id is assigned automatically
     * @param user
     * @return
     */
    @PostMapping("/createUser")
    public String createUser(@Valid @RequestBody User user) {
        System.out.println("Incoming user: " + user);
        User user1=userService.createUser(user);
        return "User created successfully";
    }

    /**
     * Get request for fetching all existing users in db
     * @return
     */
    @GetMapping("/allUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Delete request for deleting an user
     * @param id
     * @return
     */
    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "Deleted Successfully";
    }

    /**
     * Core Login functionality
     * @param loginRequest
     * @return
     */
    @PostMapping(value="/login",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Long userID = userService.authenticate(loginRequest);
        if (userID!=-1L) {
            return ResponseEntity.ok(new LoginResponse(userID, "success"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(userID, "Invalid credentials"));
        }
    }


    /**
     * Outputs user summary of unsettled expenses on groups page
     * @param id
     * @return
     */
    @GetMapping(value="/{id}/summary",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSummary(@PathVariable Long id){
        List<Float> lis= userService.getSummary(id);
        return ResponseEntity.ok(new UserSummary(lis.get(0),lis.get(1)));
    }

}
