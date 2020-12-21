package com.example.Lab1redone;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/employee")
    String asd(){
        return "asd";
    }
}
