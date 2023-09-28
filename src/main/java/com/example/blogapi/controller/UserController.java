package com.example.blogapi.controller;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("")
    public List<BlogUser> findAll(){
        return userService.findAll();
    }
    @GetMapping("/{id}")
    public BlogUser findById(@PathVariable int id){
        return userService.findById(id).get();
    }
    @PostMapping("")
    @Transactional
    public ResponseEntity<BlogUser> save(@RequestBody BlogUser blogUser){
        userService.save(blogUser);
        return new ResponseEntity<>(blogUser, HttpStatus.CREATED);
    }
    @PutMapping("")
    @Transactional
    public ResponseEntity<BlogUser> updateUser(@RequestBody BlogUser blogUser){
        BlogUser oldUser = userService.findById(blogUser.getId()).orElse(null);
        if(oldUser == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        oldUser.setFirstName(blogUser.getFirstName());
        oldUser.setLastName(blogUser.getLastName());
        oldUser.setEmail(blogUser.getEmail());
        userService.save(oldUser);
        return new ResponseEntity<>(oldUser, HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> delete(@PathVariable int id){
        userService.deleteById(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
    }

}
