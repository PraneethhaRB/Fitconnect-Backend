package com.fitconnect.fitconnect_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Id
private Long id;
@Column(nullable = false)
private String name;
@Column(nullable = false,unique = true)
private String email;
@Column(nullable = false)
private String password;
private String avatarColor;

private String goalText;

private Integer goalProgress = 0;
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Role role= Role.USER;
@Column(nullable = false,updatable = false)
private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
private Integer checkInCount = 0;
private LocalDateTime lastCheckInAt;
private Integer currentStreak=0;
private Integer longestStreak=0;
@Enumerated(EnumType.STRING)
private GoalCategory goalCategory = GoalCategory.GENERAL_FITNESS;

}
