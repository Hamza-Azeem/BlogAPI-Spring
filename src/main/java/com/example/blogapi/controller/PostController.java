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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Autowired
    public PostController(PostRepository postService, UserRepository userService){
        this.postRepository = postService;
        this.userRepository = userService;
    }
    @GetMapping("")
    public List<Post> findAll(){
        return postRepository.findAll();
    }
    @GetMapping("/{id}")
    public Post findById(@PathVariable String id){
        Post post = new Post();
        try {
            int postId = Integer.parseInt(id);
            post = (postRepository.findById(postId)).orElse(null);
        } catch (Exception exception){
            throw  new BadRequestException("Invalid way to access data.");
        }
        if(post == null){
            throw new GenericNotFoundException(String.format("No post with id[%s] was found.", id));
        }
        return post;
    }
    @GetMapping("/user")
    public List<Post> findAllPostsByUser(Authentication authentication){
        String authenticatedUserEmail = authentication.getName();
        BlogUser authenticatedUser = userRepository.findByEmail(authenticatedUserEmail).get();
        return postRepository.findByBlogUser(authenticatedUser);
    }
    @PostMapping("")
    @Transactional
    public ResponseEntity<Post> save(@RequestBody Post post, Authentication authentication){
        String authenticatedUserEmail = authentication.getName();
        BlogUser blogUser = userRepository.findByEmail(authenticatedUserEmail).get();
        post.setBlogUser(blogUser);
        postRepository.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @PutMapping("")
    @Transactional
    public ResponseEntity<Post> updatePost(@RequestBody Post post, Authentication authentication) throws AccessDeniedException {
        Post oldPost = postRepository.findById(post.getId()).orElse(null);
        String authenticatedUserEmail = authentication.getName();
        if(oldPost == null)
        {
            throw new GenericNotFoundException(String.format("No post with id[%s] was found.", post.getId()));

        }
        String postOwnerEmail = oldPost.getBlogUser().getEmail();
        if(!postOwnerEmail.equals(authenticatedUserEmail)){
            throw new AccessDeniedException("You can't delete another user's post.");
        }
        oldPost.setContent(post.getContent());
        postRepository.save(oldPost);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteById(@PathVariable String id, Authentication authentication) throws AccessDeniedException {
        String authenticatedUserEmail = authentication.getName();
        int postId;
        try {
            postId = Integer.parseInt(id);
        }catch (Exception e){
            throw  new BadRequestException("Invalid way to delete data.");
        }
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null){
            throw new GenericNotFoundException(String.format("Not post with id[%s} was found.", id));
        }
        String postOwnerEmail = post.getBlogUser().getEmail();
        if(!postOwnerEmail.equals(authenticatedUserEmail)){
            throw new AccessDeniedException("You can't update another user's post.");
        }
        postRepository.deleteById(postId);
        return new ResponseEntity<>(String.format("Post with id [%s] has been deleted", id), HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/user")
    @Transactional
    public ResponseEntity<String> deleteByUser(Authentication authentication){
        BlogUser authenticatedUser = userRepository.findByEmail(authentication.getName()).orElse(null);
        postRepository.deleteByBlogUser(authenticatedUser);
        return new ResponseEntity<>(String.format("All posts by user '%s' have been deleted.",authenticatedUser.getFirstName())
                ,HttpStatus.NO_CONTENT);
    }


}
