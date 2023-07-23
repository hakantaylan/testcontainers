package com.merikan.testcontainers.todo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Todo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    private String note;
    private String owner;
    private boolean finished;

}
