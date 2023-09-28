package com.example.blogapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public Greeting index(@RequestParam(value = "name", defaultValue = "World") String name){
        return new Greeting(1, String.format("Hello %s", name));
    }
}
