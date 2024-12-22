package com.splitwise.splitwisespring.service;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import com.splitwise.splitwisespring.model.UserGroup;
import com.splitwise.splitwisespring.model.User;
import com.splitwise.splitwisespring.model.GroupExpense;
import com.splitwise.splitwisespring.repository.GroupRepository;
import com.splitwise.splitwisespring.repository.UserRepository;
import com.splitwise.splitwisespring.repository.GroupExpenseRepository;
import com.splitwise.splitwisespring.service.Groups.GroupServiceImpl;


import java.util.*;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupExpenseRepository groupExpenseRepository;

    @InjectMocks
    private GroupServiceImpl groupServiceImpl;

    @Test
    public void CreateGroupTest() {
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName("Group1");
        userGroup.setGroupMembers(Arrays.asList(1L, 2L));

        UserGroup existingGroup = new UserGroup();
        existingGroup.setGroupId(1L);

        when(groupRepository.save(userGroup)).thenReturn(existingGroup);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(groupExpenseRepository.save(any(GroupExpense.class))).thenReturn(new GroupExpense());

        Long result = groupServiceImpl.createGroup(userGroup);
        assertThat(result).isEqualTo(1L);
        verify(groupRepository, times(1)).save(userGroup);
        verify(groupExpenseRepository, times(2)).save(any(GroupExpense.class));
    }

    @Test
    public void UpdateGroupWhenGroupExistsTest() {
        UserGroup existingGroup = new UserGroup();
        existingGroup.setGroupId(1L);
        existingGroup.setGroupName("Group1");
        existingGroup.setGroupMembers(Arrays.asList(1L, 2L));

        UserGroup updatedGroup = new UserGroup();
        updatedGroup.setGroupName("UpdatedGroup");
        updatedGroup.setGroupMembers(Arrays.asList(2L, 3L));

        given(groupRepository.findById(1L)).willReturn(Optional.of(existingGroup));
        given(userRepository.findById(2L)).willReturn(Optional.of(new User()));
        given(userRepository.findById(3L)).willReturn(Optional.of(new User()));
        given(groupExpenseRepository.findByGroupIdAndUserId(1L, 2L)).willReturn(new GroupExpense());
        given(groupExpenseRepository.findByGroupIdAndUserId(1L, 3L)).willReturn(null);
        given(groupRepository.save(existingGroup)).willReturn(existingGroup);

        UserGroup result = groupServiceImpl.updateGroup(updatedGroup, 1L);

        assertThat(result.getGroupName()).isEqualTo("UpdatedGroup");
        verify(groupExpenseRepository, times(1)).deleteByGroupIdAndUserId(1L, 1L);
        verify(groupExpenseRepository, times(1)).save(any(GroupExpense.class));
    }

    @Test
    public void UpdateGroupWhenGroupDoesNotExistTest(){
        given(groupRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> groupServiceImpl.updateGroup(new UserGroup(),1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteGroup_GroupExists() {
        UserGroup userGroup = new UserGroup(1L,"Family",Arrays.asList(1L, 2L));

        given(groupRepository.findById(1L)).willReturn(Optional.of(userGroup));
        doNothing().when(groupRepository).deleteById(userGroup.getGroupId());

        groupServiceImpl.deleteGroup(userGroup.getGroupId());

        verify(groupRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).deleteById(userGroup.getGroupId());
    }

    @Test
    public void DeleteGroupWhenGroupDoesNotExistTest(){
        given(groupRepository.findById(1L)).willReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> groupServiceImpl.deleteGroup(1L));
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void GetAllGroupsTest() {
        UserGroup group1 = new UserGroup(1L,"Family",Arrays.asList(1L, 2L));

        UserGroup group2 = new UserGroup(2L,"Uni Group",Arrays.asList(3L, 4L));


        List<UserGroup> groups = Arrays.asList(group1, group2);
        when(groupRepository.findAll()).thenReturn(groups);

        List<UserGroup> result = groupServiceImpl.getAllGroups();

        assertEquals(2, result.size());
        assertThat(result.get(0).getGroupName()).isEqualTo("Family Group");
        assertThat(result.get(1).getGroupName()).isEqualTo("Uni Group");
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    public void GetGroupByIdWhenGroupExistsTest() {
        UserGroup group = new UserGroup();
        group.setGroupId(1L);
        group.setGroupName("G1");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        UserGroup result = groupServiceImpl.getGroupById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isEqualTo(1L);
        assertThat(result.getGroupName()).isEqualTo("G1");
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    public void GetGroupByIdWhenGroupDoesNotExistTest() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> groupServiceImpl.getGroupById(1L));
        verify(groupRepository, times(1)).findById(1L);
    }


}
