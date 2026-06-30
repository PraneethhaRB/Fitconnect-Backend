package com.fitconnect.fitconnect_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitconnect.fitconnect_backend.entity.Communityy;
import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Communityy, Long> {
List<Communityy> findByGoalFocus(String name);
}
