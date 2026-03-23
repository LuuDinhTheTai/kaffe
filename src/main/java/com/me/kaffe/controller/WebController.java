package com.me.kaffe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/shipment")
    public String shipment() {
        return "shipment"; // Might not have been requested explicitly but navbar has it
    }
}
