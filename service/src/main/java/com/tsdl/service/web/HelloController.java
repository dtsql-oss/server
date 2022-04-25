package com.tsdl.service.web;

import com.tsdl.infrastructure.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final DataService dataService;

    @Autowired
    public HelloController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public String world() {
        return "Hello, World!";
    }

    @GetMapping("fail")
    public String fail() {
        dataService.get();
        return "unreachable";
    }
}
