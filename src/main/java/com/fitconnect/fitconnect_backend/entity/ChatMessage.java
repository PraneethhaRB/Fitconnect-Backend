package com.fitconnect.fitconnect_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="chat_messages")
public class ChatMessage {
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Id
private Long id;
@ManyToOne
@JoinColumn(name="sender_id",nullable=false)
private User sender;
@ManyToOne
@JoinColumn(name="community_id",nullable=false)
private Communityy community;
@Column(length = 2000)
    private String text;
private String imageUrl;
private String imageCaption;
@Column(nullable=false,updatable=false)
private LocalDateTime sentAt = LocalDateTime.now();

}
