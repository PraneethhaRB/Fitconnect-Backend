package com.fitconnect.fitconnect_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitconnect.fitconnect_backend.entity.Membership;
import com.fitconnect.fitconnect_backend.entity.MembershipStatus;

import java.util.List;
import java.util.Optional;
@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserIdAndCommunityId(Long userId, Long communityId);
    List<Membership> findByUserIdAndStatus(Long userId, MembershipStatus status);
    List<Membership> findByCommunityIdAndStatus(Long communityId, MembershipStatus status);
    boolean existsByUserIdAndCommunityId(Long userId, Long communityId);
    long countByCommunityIdAndStatus(Long communityId, MembershipStatus status);

}
