package com.fitconnect.fitconnect_backend.dto.response;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabOfferResponse {
    private Long id;
    private String labName;
    private String distance;
    private String offerText;
    private String testType;
    private String validUntil;
}