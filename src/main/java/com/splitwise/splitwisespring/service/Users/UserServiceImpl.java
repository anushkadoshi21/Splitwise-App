package com.splitwise.splitwisespring.service.Users;

import com.splitwise.splitwisespring.model.ApiRequest.LoginRequest;
import com.splitwise.splitwisespring.model.ApiResponse.UserSummary;
import com.splitwise.splitwisespring.repository.GroupExpenseRepository;
import com.splitwise.splitwisespring.repository.UserRepository;
import com.splitwise.splitwisespring.model.User;

import java.util.ArrayList;
import java.util.List;

import com.splitwise.splitwisespring.repository.UserToUserExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
     //embedding repository
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserToUserExpenseRepository userToUserExpenseRepository;
    @Autowired
    private GroupExpenseRepository groupExpenseRepository;



    /**
     * Method to create a new user, UserId is assigned automatically
     * Only username to be provided
     * @param user
     * @return
     */
    @Override
    @Transactional
    public User createUser(User user)  {
        User savedUser= userRepository.save(user);
        System.out.println("Saved user: " + savedUser);
        return savedUser;
    }

    /**
     * Fetches all users in db
     * @return
     */
    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    /**
     * Deletes record of user with given userID after checking if the userId exists
     * @param userId
     */
    @Override
    @Transactional
    public void deleteUserById(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(userId);
    }

    /**
     * Authenticate functionality to check if username & password combination is valid
     * @param loginRequest
     * @return
     */
    @Override
    public Long authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByUserName(loginRequest.getUserName());
        if (user != null && loginRequest.getPassword().equals(user.getPassword())) {
            return user.getUserId();
        }
        return -1L;
    }

    /**
     * Gets User summary considering all groups expenses
     * @param id
     * @return
     */
    @Override
    public List<Float> getSummary(Long id) {
        float gives= groupExpenseRepository.sumByUserId(id);
        float gets= userToUserExpenseRepository.sumByUserId(id);
        List<Float> lis= new ArrayList();
        lis.add(gives);
        lis.add(gets);
        return lis;
    }

}
