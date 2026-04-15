
package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.LegacyUserProcessor;
import com.example.usermanagement.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final LegacyUserProcessor legacyUserProcessor;

    public UserController(UserService service, LegacyUserProcessor legacyUserProcessor) {
        this.service = service;
        this.legacyUserProcessor = legacyUserProcessor;
    }

    @GetMapping
    public List<User> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public User one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public User create(@RequestBody User user) { return service.save(user); }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return service.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PostMapping("/legacy")
    public User legacyCreate(
            @RequestBody User user,
            @RequestParam(defaultValue = "false") boolean forceSave,
            @RequestParam(required = false) String sourceSystem) {

        // mauvaise pratique : logique dans le controller
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName("UNKNOWN");
        }

        return legacyUserProcessor.processUser(user, forceSave, sourceSystem);
    }
}
