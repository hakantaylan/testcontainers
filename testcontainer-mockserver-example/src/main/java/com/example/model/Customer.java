package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Data
@Entity(name = "customer")
public class Customer {

    @Id
    @Column(name = "email")
    @JsonProperty("email")
    @NotEmpty(message = "The 'email' field is required")
    @Email(message = "The 'email' field is invalid")
    private String email;

    @Column(name = "name")
    @JsonProperty("name")
    @NotEmpty(message = "The 'name' field is required")
    private String name;

}
