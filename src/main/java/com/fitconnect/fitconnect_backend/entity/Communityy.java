package com.fitconnect.fitconnect_backend.entity;

import java.time.LocalDateTime;
import com.fitconnect.fitconnect_backend.entity.User;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="communities")
public class Communityy {

   @GeneratedValue(strategy=GenerationType.IDENTITY)
   @Id
   private Long id;
   @Column(nullable=false)
   private String name;
   @Column(length=1000)
   private String description;

   private String coverColor;
   @ManyToOne
   @JoinColumn(name="admin_id",nullable=false)
    private User admin;
    @Column(nullable=false)
    private String goalFocus;
   @Column(nullable=false,updatable=false)
   private LocalDateTime createdAt = LocalDateTime.now();

}
