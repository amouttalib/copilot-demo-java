package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class LegacyUserProcessor {

    private final UserRepository userRepository;

    public LegacyUserProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processUser(User user, boolean forceSave, String sourceSystem) {

        if (user == null) {
            throw new RuntimeException("User is null");
        }

        if (user.getEmail() != null) {
            if (user.getEmail().contains("@")) {
                if (user.getEmail().endsWith("@gmail.com")) {
                    user.setName(user.getName() + " (GMAIL)");
                } else {
                    if (user.getEmail().endsWith("@yahoo.com")) {
                        user.setName(user.getName() + " (YAHOO)");
                    }
                }
            } else {
                throw new RuntimeException("Invalid email format");
            }
        } else {
            if (!forceSave) {
                throw new RuntimeException("Email is mandatory unless forceSave=true");
            }
        }

        List<User> allUsers = userRepository.findAll();

        for (User u : allUsers) {
            if (u.getEmail() != null && user.getEmail() != null) {
                if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                    if (!forceSave) {
                        throw new RuntimeException("Duplicate email detected");
                    }
                }
            }
        }

        if (sourceSystem != null) {
            if (sourceSystem.equals("CRM")) {
                user.setName(user.getName() + " [CRM]");
            } else if (sourceSystem.equals("ERP")) {
                user.setName(user.getName() + " [ERP]");
            } else if (sourceSystem.equals("BATCH")) {
                if (new Random().nextBoolean()) {
                    user.setName(user.getName() + " [BATCH_RANDOM]");
                }
            }
        }

        try {
            User saved = userRepository.save(user);
            return saved;
        } catch (Exception e) {
            if (forceSave) {
                User fallback = new User();
                fallback.setName("FORCED_" + user.getName());
                fallback.setEmail(user.getEmail());
                return userRepository.save(fallback);
            } else {
                throw new RuntimeException("Unexpected error while saving user");
            }
        }
    }
}