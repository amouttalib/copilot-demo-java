package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_shouldReturnUsersFromRepository() {
        List<User> expectedUsers = Arrays.asList(new User(), new User());
        when(repository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.findAll();

        assertEquals(expectedUsers, result);
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyListWhenRepositoryIsEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.findAll();

        assertEquals(Collections.emptyList(), result);
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnUserWhenFound() {
        Long id = 1L;
        User expectedUser = new User();
        when(repository.findById(id)).thenReturn(Optional.of(expectedUser));

        User result = userService.findById(id);

        assertEquals(expectedUser, result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        User result = userService.findById(id);

        assertNull(result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void save_shouldDelegateToRepository() {
        User input = new User();
        User persisted = new User();
        when(repository.save(input)).thenReturn(persisted);

        User result = userService.save(input);

        assertEquals(persisted, result);
        verify(repository, times(1)).save(input);
    }

    @Test
    void delete_shouldDelegateToRepository() {
        Long id = 3L;

        userService.delete(id);

        verify(repository, times(1)).deleteById(id);
    }
}

