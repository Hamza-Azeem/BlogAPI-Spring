package com.example.blogapi.controller;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.exceptions.BadRequestException;
import com.example.blogapi.exceptions.GenericNotFoundException;
import com.example.blogapi.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/users")
@Validated
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
    public BlogUser findById(@PathVariable String id){
        BlogUser user = new BlogUser();
        try{
            int userId = Integer.parseInt(id);
            user = userService.findById(userId).get();
        }
        catch (NoSuchElementException exception){
            throw new GenericNotFoundException(String.format("No user found with id [%s] in our database.", id));
        }
        catch (Exception exception){
            throw new BadRequestException("Invalid way to access data.");
        }
        return user;
    }
    @PostMapping("")
    @Transactional
    public ResponseEntity<BlogUser> save(@RequestBody BlogUser blogUser){
        if(blogUser == null){
            throw new BadRequestException("Invalid way to save data.");
        }
        try {
            userService.save(blogUser);
        }
        catch (Exception e){
            throw new BadRequestException("Invalid way to save data.");
        }
        return new ResponseEntity<>(blogUser, HttpStatus.CREATED);
    }
    @PutMapping("")
    @Transactional
    public ResponseEntity<BlogUser> updateUser(@RequestBody BlogUser blogUser){
        BlogUser oldUser = userService.findById(blogUser.getId()).orElse(null);
        if(oldUser == null){
            throw new GenericNotFoundException(String.format("No user with id[%s] was found to update.", blogUser.getId()));
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
