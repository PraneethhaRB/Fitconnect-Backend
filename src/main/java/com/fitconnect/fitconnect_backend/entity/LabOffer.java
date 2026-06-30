package com.fitconnect.fitconnect_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lab_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabOffer {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String labName;
    private String distance;
    private String offerText;
    private String testType;
    private String validUntil;
    private Double latitude;
    private Double longitude;
}
