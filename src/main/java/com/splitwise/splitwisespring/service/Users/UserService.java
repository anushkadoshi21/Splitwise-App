package com.splitwise.splitwisespring.service.Users;

import com.splitwise.splitwisespring.model.ApiRequest.LoginRequest;
import com.splitwise.splitwisespring.model.ApiResponse.UserSummary;
import com.splitwise.splitwisespring.model.User;
import java.util.List;

public interface UserService {

    /**
     * Method to create a new user, UserId is assigned automatically
     *      * Only username to be provided
     * @param user
     * @return
     */
    User createUser(User user);

    /**
     * Fetches all users in db
     * @return
     */
    List<User> getAllUsers();

    /**
     * Deletes record of user with given userID after checking if the userId exists
     * @param UserId
     */
    void deleteUserById(Long UserId);

    /**
     * Authenticate functionality to check if username & password combination is valid
     * @param loginRequest
     * @return
     */
    Long authenticate(LoginRequest loginRequest);

    /**
     * Gets User summary considering all groups expenses
     * @param id
     * @return
     */
    List<Float> getSummary(Long id);
}
