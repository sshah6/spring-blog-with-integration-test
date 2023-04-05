package com.codeup.zenithspringblog.repositories;

import com.codeup.zenithspringblog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}