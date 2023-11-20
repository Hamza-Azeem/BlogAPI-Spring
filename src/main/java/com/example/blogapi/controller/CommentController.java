package com.example.blogapi.controller;
import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.entity.Comment;
import com.example.blogapi.entity.Post;
import com.example.blogapi.exceptions.BadRequestException;
import com.example.blogapi.exceptions.GenericNotFoundException;
import com.example.blogapi.repository.CommentRepository;
import com.example.blogapi.repository.PostRepository;
import com.example.blogapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentRepository commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Autowired
    public CommentController(CommentRepository commentService, PostRepository postService, UserRepository userService){
        this.commentService = commentService;
        this.postRepository = postService;
        this.userRepository = userService;
    }
    @GetMapping("")
    public List<Comment> findAll(){
        return commentService.findAll();
    }
    @GetMapping("/{id}")
    public Comment findComment(@PathVariable String id){
        Comment comment = new Comment();
        try{
            int commentId = Integer.parseInt(id);
            comment = commentService.findById(commentId).orElse(null);
        }
        catch (Exception e){
            throw new BadRequestException("Invalid way to access data.");
        }
        if(comment == null){
            throw new GenericNotFoundException(String.format("No comment with id[%s] was found.", id));
        }
        return comment;
    }
    @GetMapping("/post/{id}")
    public ResponseEntity<List<Comment>> findAllCommentsOnPost(@PathVariable String id){
        Post post = new Post();
        try{
            int postId = Integer.parseInt(id);
            post = postRepository.findById(postId).orElse(null);
        }catch (Exception e){
            throw  new BadRequestException("Invalid way to access data.");
        }
        if(post == null){
            throw new GenericNotFoundException(String.format("No post with id[%s] was found.", id));
        }
        return new ResponseEntity<>(commentService.findByPost(post), HttpStatus.ACCEPTED);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Comment>> findAllCommentsByUser(@PathVariable String id){
        BlogUser blogUser = new BlogUser();
        try{
            int userId = Integer.parseInt(id);
            blogUser = userRepository.findById(userId).orElse(null);
        }catch (Exception e){
            throw  new BadRequestException("Invalid way to access data.");
        }
        if(blogUser == null){
            throw new GenericNotFoundException(String.format("No user with id[%s] was found.", id));
        }
        return new ResponseEntity<>(commentService.findByBlogUser(blogUser), HttpStatus.ACCEPTED);
    }
    @PostMapping("")
    @Transactional
    public ResponseEntity<Comment> save(@RequestParam String postViewed, @RequestBody Comment comment, Authentication authentication)
    {
        Post post = new Post();
        String authenticatedUserEmail = authentication.getName();
        BlogUser commentOwner = userRepository.findByEmail(authenticatedUserEmail).get();
        try{
            int postId = Integer.parseInt(postViewed);
            post = postRepository.findById(postId).orElse(null);
        }catch (Exception e){
            throw  new BadRequestException("Invalid way to access data.");
        }
        if(post == null){
            throw new GenericNotFoundException(String.format("No post with id[%s] was found.", postViewed));
        }
        comment.setBlogUser(commentOwner);
        commentService.save(comment);
        post.addComment(comment);
        postRepository.save(post);
        commentOwner.addComment(comment);
        userRepository.save(commentOwner);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment comment, Authentication authentication){
        String authenticatedUserEmail = authentication.getName();
        Comment oldComment = new Comment();
        try{
            int commentId = Integer.parseInt(id);
            oldComment = commentService.findById(commentId).orElse(null);
        }
        catch (Exception e){
            throw new BadRequestException("Invalid way to access data.");
        }
        if(oldComment == null){
            throw new GenericNotFoundException(String.format("No comment with id[%s] was found.", id));
        }
        BlogUser commentOwner = oldComment.getBlogUser();
        if(!authenticatedUserEmail.equals(commentOwner.getEmail())){
            throw new AccessDeniedException("You can't edit another user's comment.");
        }
        oldComment.setContent(comment.getContent());
        commentService.save(oldComment);
        return new ResponseEntity<>(oldComment, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteComment(@PathVariable int id, Authentication authentication){
        String authenticatedUserEmail = authentication.getName();
        BlogUser commentOwner = commentService.findById(id).get().getBlogUser();
        if(!authenticatedUserEmail.equals(commentOwner.getEmail())){
            throw new AccessDeniedException("You can't delete another user's comment.");
        }
        commentService.deleteById(id);
        return new ResponseEntity<>("Comment removed", HttpStatus.NO_CONTENT);
    }

}
