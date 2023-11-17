package com.example.blogapi.controller;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.entity.Post;
import com.example.blogapi.exceptions.BadRequestException;
import com.example.blogapi.exceptions.GenericNotFoundException;
import com.example.blogapi.repository.PostRepository;
import com.example.blogapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository postService;
    private final UserRepository userService;
    @Autowired
    public PostController(PostRepository postService, UserRepository userService){
        this.postService = postService;
        this.userService = userService;
    }
    @GetMapping("")
    public List<Post> findAll(){
        return postService.findAll();
    }
    @GetMapping("/{id}")
    public Post findById(@PathVariable String id){
        Post post = new Post();
        try {
            int postId = Integer.parseInt(id);
            post = (postService.findById(postId)).orElse(null);
        } catch (Exception exception){
            throw  new BadRequestException("Invalid way to access data.");
        }
        if(post == null){
            throw new GenericNotFoundException(String.format("No post with id[%s] was found.", id));
        }
        return post;
    }
    @GetMapping("/user/{id}")
    public List<Post> findAllPostsByUser(@PathVariable String id){
        BlogUser blogUser = new BlogUser();
        try {
            int userId = Integer.parseInt(id);
            blogUser = userService.findById(userId).orElse(null);
        }catch (Exception exception){
            throw new BadRequestException("Invalid way to access data.");
        }
        if(blogUser == null){
            throw new GenericNotFoundException(String.format("No user with id[%s] was found.", id));
        }
        return postService.findByBlogUser(blogUser);
    }
    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<Post> save(@PathVariable String id,@RequestBody Post post){
        BlogUser blogUser = new BlogUser();
        try{
            int userId = Integer.parseInt(id);
            blogUser = userService.findById(userId).orElse(null);
        } catch (Exception exception){
            throw  new BadRequestException("Invalid way to save data.");
        }
        if(blogUser == null){
            throw new GenericNotFoundException(String.format("No user with id[%s] was found.", id));
        }
        post.setBlogUser(blogUser);
        postService.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @PutMapping("")
    @Transactional
    public ResponseEntity<Post> updatePost(@RequestBody Post post){
        Post oldPost = postService.findById(post.getId()).orElse(null);
        if(oldPost == null)
        {
            throw new GenericNotFoundException(String.format("No post with id[%s] was found.", post.getId()));

        }
        oldPost.setContent(post.getContent());
        postService.save(oldPost);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteById(@PathVariable int id){
        postService.deleteById(id);
        return new ResponseEntity<>(String.format("Post with id [%s] has been deleted", id), HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/user/{id}")
    @Transactional
    public ResponseEntity<String> deleteByUser(@PathVariable String id){
        BlogUser blogUser = new BlogUser();
        try{
            int userId = Integer.parseInt(id);
            blogUser = userService.findById(userId).orElse(null);
        }catch (Exception e){
            throw  new BadRequestException("Invalid way to delete data.");
        }
        if(blogUser == null){
            throw new GenericNotFoundException(String.format("No user with id[%s] was found.", id));
        }
        postService.deleteByBlogUser(blogUser);
        return new ResponseEntity<>(String.format("All posts by user '%s' have been deleted.",blogUser.getFirstName())
                ,HttpStatus.NO_CONTENT);
    }


}
