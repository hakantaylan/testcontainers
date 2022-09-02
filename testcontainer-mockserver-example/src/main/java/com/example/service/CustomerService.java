package com.example.service;

import com.example.model.Customer;
import com.example.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final EmailService emailService;
    private final CustomerRepository repository;

    public void register(Customer customer) {
        emailService.sendWelcomeEmail(customer.getEmail());
        repository.save(customer);
    }

}
