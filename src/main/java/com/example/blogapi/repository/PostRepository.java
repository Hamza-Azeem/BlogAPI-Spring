package com.example.blogapi.repository;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByBlogUser(BlogUser blogUser);
    void deleteByBlogUser(BlogUser blogUser);
}
