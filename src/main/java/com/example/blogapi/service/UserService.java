package com.example.blogapi.service;

import com.example.blogapi.entity.BlogUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends JpaRepository<BlogUser, Integer> {
}
