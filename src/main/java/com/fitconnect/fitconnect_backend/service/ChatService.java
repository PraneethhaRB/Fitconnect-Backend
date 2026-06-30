package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.dto.response.ChatMessageResponse;
import com.fitconnect.fitconnect_backend.entity.*;
import com.fitconnect.fitconnect_backend.exception.ForbiddenActionException;
import com.fitconnect.fitconnect_backend.exception.ResourceNotFoundException;
import com.fitconnect.fitconnect_backend.repository.*;
import com.fitconnect.fitconnect_backend.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final StorageService storageService;
    private final SimpMessagingTemplate messagingTemplate;

    public List<ChatMessageResponse> getHistory(String email, Long communityId, Long beforeId) {
        verifyIsApprovedMember(email, communityId);

        List<ChatMessage> messages;
        PageRequest page = PageRequest.of(0, 50);

        if (beforeId == null) {
            messages = chatMessageRepository.findByCommunityIdOrderBySentAtDesc(communityId, page);
        } else {
            messages = chatMessageRepository.findByCommunityIdAndIdLessThanOrderBySentAtDesc(communityId, beforeId, page);
        }

        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ChatMessageResponse sendMessage(String email, Long communityId, String text, String imageCaption, MultipartFile image) {
        User sender = verifyIsApprovedMember(email, communityId);

        if ((text == null || text.isBlank()) && (image == null || image.isEmpty())) {
            throw new IllegalArgumentException("Message must have text or an image");
        }

        Communityy community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        ChatMessage message = new ChatMessage();
        message.setCommunity(community);
        message.setSender(sender);
        message.setText(text);
        message.setImageCaption(imageCaption);

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.store(image, communityId);
            message.setImageUrl(imageUrl);
        
            // This counts as a progress check-in
            sender.setCheckInCount(sender.getCheckInCount() + 1);
            sender.setLastCheckInAt(java.time.LocalDateTime.now());
        
            int newProgress = Math.min(100, sender.getCheckInCount() * 5); // each check-in = +5%, capped at 100
            sender.setGoalProgress(newProgress);
        
            userRepository.save(sender);
        }
        ChatMessage saved = chatMessageRepository.save(message);
        ChatMessageResponse response = toResponse(saved);

        // Push to everyone subscribed to this community's chat topic
        messagingTemplate.convertAndSend("/topic/community/" + communityId, response);

        return response;
    }

    private User verifyIsApprovedMember(String email, Long communityId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Membership membership = membershipRepository.findByUserIdAndCommunityId(user.getId(), communityId)
                .orElseThrow(() -> new ForbiddenActionException("You are not a member of this community"));

        if (membership.getStatus() != MembershipStatus.APPROVED) {
            throw new ForbiddenActionException("You are not an approved member of this community");
        }

        return user;
    }

    private ChatMessageResponse toResponse(ChatMessage m) {
        return new ChatMessageResponse(
                m.getId(),
                m.getSender().getId(),
                m.getSender().getName(),
                m.getSender().getAvatarColor(),
                m.getText(),
                m.getImageUrl(),
                m.getImageCaption(),
                m.getSentAt()
        );
    }
}