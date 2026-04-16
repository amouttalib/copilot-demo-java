package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.LegacyUserProcessor;
import com.example.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private LegacyUserProcessor legacyUserProcessor;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService, legacyUserProcessor);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    // Tests pour all()
    @Test
    void all_shouldReturnAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(userService, times(1)).findAll();
    }

    @Test
    void all_shouldReturnEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).findAll();
    }

    // Tests pour one(id)
    @Test
    void one_shouldReturnUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void one_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/users/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService, times(1)).findById(99L);
    }

    // Tests pour create()
    @Test
    void create_shouldCreateNewUser() throws Exception {
        User inputUser = new User();
        inputUser.setName("Alice Johnson");
        inputUser.setEmail("alice@example.com");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("Alice Johnson");
        savedUser.setEmail("alice@example.com");

        when(userService.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Alice Johnson"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void create_shouldCreateUserWithNullName() throws Exception {
        User inputUser = new User();
        inputUser.setEmail("test@example.com");

        User savedUser = new User();
        savedUser.setId(4L);
        savedUser.setEmail("test@example.com");

        when(userService.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk());

        verify(userService, times(1)).save(any(User.class));
    }

    // Tests pour update()
    @Test
    void update_shouldUpdateExistingUser() throws Exception {
        Long userId = 1L;
        User inputUser = new User();
        inputUser.setName("John Updated");
        inputUser.setEmail("john.updated@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");

        when(userService.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

        verify(userService, times(1)).save(argThat(user -> user.getId().equals(userId)));
    }

    @Test
    void update_shouldSetIdFromPathVariable() throws Exception {
        Long userId = 5L;
        User inputUser = new User();
        inputUser.setName("Bob Smith");
        inputUser.setEmail("bob@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Bob Smith");
        updatedUser.setEmail("bob@example.com");

        when(userService.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).save(argThat(user -> user.getId().equals(5L)));
    }

    // Tests pour delete()
    @Test
    void delete_shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void delete_shouldCallServiceWithCorrectId() throws Exception {
        mockMvc.perform(delete("/api/users/10"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(10L);
    }

    // Tests pour legacyCreate()
    @Test
    void legacyCreate_shouldCreateUserWithoutForceSaveAndSourceSystem() throws Exception {
        User inputUser = new User();
        inputUser.setName("Legacy User");
        inputUser.setEmail("legacy@example.com");

        User processedUser = new User();
        processedUser.setId(100L);
        processedUser.setName("Legacy User");
        processedUser.setEmail("legacy@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(false), isNull()))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Legacy User"));

        verify(legacyUserProcessor, times(1))
                .processUser(any(User.class), eq(false), isNull());
    }

    @Test
    void legacyCreate_shouldSetNameToUnknownWhenNull() throws Exception {
        User inputUser = new User();
        inputUser.setEmail("test@example.com");

        User processedUser = new User();
        processedUser.setId(101L);
        processedUser.setName("UNKNOWN");
        processedUser.setEmail("test@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(false), isNull()))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk());

        verify(legacyUserProcessor, times(1))
                .processUser(argThat(user -> "UNKNOWN".equals(user.getName())), eq(false), isNull());
    }

    @Test
    void legacyCreate_shouldSetNameToUnknownWhenEmpty() throws Exception {
        User inputUser = new User();
        inputUser.setName("   ");
        inputUser.setEmail("test@example.com");

        User processedUser = new User();
        processedUser.setId(102L);
        processedUser.setName("UNKNOWN");
        processedUser.setEmail("test@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(false), isNull()))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk());

        verify(legacyUserProcessor, times(1))
                .processUser(argThat(user -> "UNKNOWN".equals(user.getName())), eq(false), isNull());
    }

    @Test
    void legacyCreate_shouldProcessWithForceSaveTrue() throws Exception {
        User inputUser = new User();
        inputUser.setName("Forced User");
        inputUser.setEmail("forced@example.com");

        User processedUser = new User();
        processedUser.setId(103L);
        processedUser.setName("Forced User");
        processedUser.setEmail("forced@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(true), isNull()))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy?forceSave=true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(103));

        verify(legacyUserProcessor, times(1))
                .processUser(any(User.class), eq(true), isNull());
    }

    @Test
    void legacyCreate_shouldProcessWithSourceSystemCRM() throws Exception {
        User inputUser = new User();
        inputUser.setName("CRM User");
        inputUser.setEmail("crm@example.com");

        User processedUser = new User();
        processedUser.setId(104L);
        processedUser.setName("CRM User [CRM]");
        processedUser.setEmail("crm@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(false), eq("CRM")))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy?sourceSystem=CRM")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CRM User [CRM]"));

        verify(legacyUserProcessor, times(1))
                .processUser(any(User.class), eq(false), eq("CRM"));
    }

    @Test
    void legacyCreate_shouldProcessWithSourceSystemERP() throws Exception {
        User inputUser = new User();
        inputUser.setName("ERP User");
        inputUser.setEmail("erp@example.com");

        User processedUser = new User();
        processedUser.setId(105L);
        processedUser.setName("ERP User [ERP]");
        processedUser.setEmail("erp@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(false), eq("ERP")))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy?sourceSystem=ERP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ERP User [ERP]"));

        verify(legacyUserProcessor, times(1))
                .processUser(any(User.class), eq(false), eq("ERP"));
    }

    @Test
    void legacyCreate_shouldProcessWithBothForceSaveAndSourceSystem() throws Exception {
        User inputUser = new User();
        inputUser.setName("Complex User");
        inputUser.setEmail("complex@example.com");

        User processedUser = new User();
        processedUser.setId(106L);
        processedUser.setName("Complex User [CRM]");
        processedUser.setEmail("complex@example.com");

        when(legacyUserProcessor.processUser(any(User.class), eq(true), eq("CRM")))
                .thenReturn(processedUser);

        mockMvc.perform(post("/api/users/legacy?forceSave=true&sourceSystem=CRM")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(106));

        verify(legacyUserProcessor, times(1))
                .processUser(any(User.class), eq(true), eq("CRM"));
    }
}

