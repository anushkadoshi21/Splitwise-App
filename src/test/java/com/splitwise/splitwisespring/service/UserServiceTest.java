package com.splitwise.splitwisespring.service;


import java.util.List;
import java.util.Optional;

import com.splitwise.splitwisespring.model.ApiRequest.LoginRequest;
import com.splitwise.splitwisespring.model.User;
import com.splitwise.splitwisespring.repository.UserRepository;
import com.splitwise.splitwisespring.service.Users.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserTest(){
        User user = new User();
        user.setUserName("Anushka");

        User user1 = new User(1L,"Anushka","123"); // Simulate the auto-generated ID

        given(userRepository.save(user)).willReturn(user1);

        User testResult= userService.createUser(user);
        assertThat(testResult.getUserName()).isEqualTo("Anushka");
        assertThat(testResult.getUserId()).isEqualTo(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getAllUsersTest(){
        User user1 = new User(1L,"Anushka","123");
        User user2 = new User(2L,"Saket","123");

        given(userRepository.findAll()).willReturn(List.of(user1,user2));
        List<User> testResult= userService.getAllUsers();
        assertThat(testResult).isNotNull();
        assertThat(testResult.size()).isEqualTo(2);
        assertThat(testResult.get(0).getUserName()).isEqualTo("Anushka");
        assertThat(testResult.get(1).getUserName()).isEqualTo("Saket");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserByIdTest(){
        User user= new User(1L,"Anushka","123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);

    }

    @Test
    void authenticateTestSuccess() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password123");
        User dummyUser = new User(1L, "testUser", "password123");

        when(userRepository.findByUserName("testUser")).thenReturn(dummyUser);
        Long userId = userService.authenticate(loginRequest);

        assertThat(userId.equals(1L)).isTrue();
        verify(userRepository, times(1)).findByUserName("testUser");
    }

}
