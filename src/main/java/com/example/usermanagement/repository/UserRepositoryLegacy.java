
package com.example.usermanagement.repository;


import com.example.usermanagement.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserRepositoryLegacy {

    @PersistenceContext
    private EntityManager entityManager;

    // ❌ SQL Injection vulnerability
    public User findByEmailUnsafe(String email) {
        String query = "SELECT u FROM User u WHERE u.email = '" + email + "'";
        return (User) entityManager.createQuery(query).getSingleResult();
    }
}
