package com.example.blogapi.repository;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.entity.Comment;
import com.example.blogapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);
    List<Comment> findByBlogUser(BlogUser blogUser);
}
