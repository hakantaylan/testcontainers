package com.example.controller;

import com.example.service.CustomerService;
import com.example.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping("/customers")
    public void register(@Valid @RequestBody Customer customer) {
        service.register(customer);
    }

    @GetMapping("/deneme")
    public String deneme() {
        return "OK";
    }

}
