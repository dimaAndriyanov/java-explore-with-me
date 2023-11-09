package ru.practicum.ewm.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private final Long id;

    @Column(name = "user_name", nullable = false)
    private final String name;

    @Column(nullable = false, unique = true)
    private final String email;
}