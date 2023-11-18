package com.example.blogapi.controller;

import com.example.blogapi.entity.BlogUser;
import com.example.blogapi.exceptions.BadRequestException;
import com.example.blogapi.exceptions.GenericNotFoundException;
import com.example.blogapi.repository.UserRepository;
import com.example.blogapi.service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserRepository userRepository;
    @Autowired
    public UserController(UserRepository userRepository, JwtService jwtService){
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public List<BlogUser> findAll(){
        return userRepository.findAll();
    }
    @GetMapping("/{id}")
    public BlogUser findById(@PathVariable String id){
        BlogUser user = new BlogUser();
        try{
            int userId = Integer.parseInt(id);
            user = userRepository.findById(userId).get();
        }
        catch (NoSuchElementException exception){
            throw new GenericNotFoundException(String.format("No user found with id [%s] in our database.", id));
        }
        catch (Exception exception){
            throw new BadRequestException("Invalid way to access data.");
        }
        return user;
    }
//    @PostMapping("")
//    @Transactional
//    public ResponseEntity<BlogUser> save(@RequestBody BlogUser blogUser){
//        if(blogUser == null){
//            throw new BadRequestException("Invalid way to save data.");
//        }
//        try {
//            userService.save(blogUser);
//        }
//        catch (Exception e){
//            throw new BadRequestException("Invalid way to save data.");
//        }
//        return new ResponseEntity<>(blogUser, HttpStatus.CREATED);
//    }
@PutMapping("")
@Transactional
public ResponseEntity<BlogUser> updateUser(@RequestBody BlogUser blogUser, Authentication authentication) throws AccessDeniedException {
    String authenticatedUsername = authentication.getName();


    if (!authenticatedUsername.equals(blogUser.getEmail())) {
        throw new AccessDeniedException("You are not allowed to update other users' information.");
    }

    BlogUser oldUser = userRepository.findById(blogUser.getId()).orElse(null);
    if (oldUser == null) {
        throw new GenericNotFoundException(String.format("No user with id[%s] was found to update.", blogUser.getId()));
    }

    oldUser.setFirstName(blogUser.getFirstName());
    oldUser.setLastName(blogUser.getLastName());
    oldUser.setEmail(blogUser.getEmail());
    userRepository.save(oldUser);

    return new ResponseEntity<>(oldUser, HttpStatus.ACCEPTED);
}

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> delete(@PathVariable int id, Authentication authentication) throws AccessDeniedException {
        String authenticatedUsername = authentication.getName();
        BlogUser user = userRepository.findById(id).get();
        if(!authenticatedUsername.equals(user.getEmail())){
            throw new AccessDeniedException("You are not allowed to delete other users.");
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT);
    }

}
