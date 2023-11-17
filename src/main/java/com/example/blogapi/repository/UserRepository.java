package com.example.blogapi.repository;

import com.example.blogapi.entity.BlogUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserRepository extends JpaRepository<BlogUser, Integer> {
    Optional<BlogUser> findByEmail(String email);
}
