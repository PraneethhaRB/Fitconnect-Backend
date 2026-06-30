package com.fitconnect.fitconnect_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitconnect.fitconnect_backend.entity.LabOffer;
@Repository
public interface LabOfferRepository extends JpaRepository<LabOffer, Long> {

}
