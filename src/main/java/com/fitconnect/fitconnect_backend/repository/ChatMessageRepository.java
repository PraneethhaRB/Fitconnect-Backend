package com.fitconnect.fitconnect_backend.repository;

import com.fitconnect.fitconnect_backend.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByCommunityIdOrderBySentAtDesc(Long communityId, Pageable pageable);

    List<ChatMessage> findByCommunityIdAndIdLessThanOrderBySentAtDesc(Long communityId, Long beforeId, Pageable pageable);
}