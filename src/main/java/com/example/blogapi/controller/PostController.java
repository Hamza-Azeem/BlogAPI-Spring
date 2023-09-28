package com.example.blogapi.controller;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.entity.Post;
import com.example.blogapi.service.PostService;
import com.example.blogapi.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    @Autowired
    public PostController(PostService postService, UserService userService){
        this.postService = postService;
        this.userService = userService;
    }
    @GetMapping("")
    public List<Post> findAll(){
        return postService.findAll();
    }
    @GetMapping("/{id}")
    public Post findById(@PathVariable int id){
        return postService.findById(id).get();
    }
    @GetMapping("/user/{id}")
    public List<Post> findAllPostsByUser(@PathVariable int id){
        BlogUser blogUser = userService.findById(id).get();
        System.out.println(blogUser);
        return postService.findByBlogUser(blogUser);
    }
    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<Post> save(@PathVariable int id,@RequestBody Post post){
        BlogUser blogUser = userService.findById(id).get();
        if(blogUser != null){
            post.setBlogUser(blogUser);
            postService.save(post);
            return new ResponseEntity<>(post, HttpStatus.CREATED);

        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("")
    @Transactional
    public ResponseEntity<Post> updatePost(@RequestBody Post post){
        Post oldPost = postService.findById(post.getId()).orElse(null);
        if(oldPost == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        oldPost.setContent(post.getContent());
        postService.save(oldPost);
        return new ResponseEntity<>(post, HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteById(@PathVariable int id){
        postService.deleteById(id);
        return new ResponseEntity<>(String.format("Post with id [%s] has been deleted", id), HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/user/{id}")
    @Transactional
    public ResponseEntity<String> deleteByUser(@PathVariable int id){
        BlogUser blogUser = userService.findById(id).get();
        postService.deleteByBlogUser(blogUser);
        return new ResponseEntity<>(String.format("All posts by user '%s' have been deleted.",blogUser.getFirstName())
                ,HttpStatus.NO_CONTENT);
    }


}
