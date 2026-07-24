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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public ChatMessageResponse sendMessage(String email, Long communityId, String text,
                                           String imageCaption, MultipartFile image) {
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

        // track milestone separately — populated only if image is sent
        String milestone = null;

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.store(image, communityId);
            message.setImageUrl(imageUrl);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastCheckIn = sender.getLastCheckInAt();

            // increment check-in count
            int newCount = (sender.getCheckInCount() != null ? sender.getCheckInCount() : 0) + 1;
            sender.setCheckInCount(newCount);
            sender.setLastCheckInAt(now);

            // streak logic
            int currentStreak = sender.getCurrentStreak() != null ? sender.getCurrentStreak() : 0;

            if (lastCheckIn == null) {
                currentStreak = 1;
            } else {
                long daysSinceLast = ChronoUnit.DAYS.between(
                        lastCheckIn.toLocalDate(), now.toLocalDate());

                if (daysSinceLast == 1) {
                    currentStreak += 1;
                } else if (daysSinceLast == 0) {
                    // same day — no change
                } else {
                    currentStreak = 1;
                }
            }

            sender.setCurrentStreak(currentStreak);

            int longest = sender.getLongestStreak() != null ? sender.getLongestStreak() : 0;
            if (currentStreak > longest) {
                sender.setLongestStreak(currentStreak);
            }

            int newProgress = Math.min(100, newCount * 5);
            sender.setGoalProgress(newProgress);

            userRepository.save(sender);

            // check milestone AFTER saving so counts are final
            milestone = checkMilestone(newCount, currentStreak);
        }

        // save the message ONCE, outside the image block
        ChatMessage saved = chatMessageRepository.save(message);

        // build response ONCE, after saved exists
        ChatMessageResponse response = toResponse(saved);

        // attach milestone if one was triggered
        if (milestone != null) {
            response.setMilestoneMessage(milestone);
        }

        // broadcast to all WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/community/" + communityId, response);

        return response;
    }

    private User verifyIsApprovedMember(String email, Long communityId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Membership membership = membershipRepository
                .findByUserIdAndCommunityId(user.getId(), communityId)
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
                m.getSentAt(),
                null // milestoneMessage defaults to null — set explicitly after if needed
        );
    }

    private String checkMilestone(int checkInCount, int streak) {
        if (checkInCount == 1)  return "First check-in! Your journey starts now 🚀";
        if (checkInCount == 5)  return "5 check-ins! You're building a habit 💪";
        if (checkInCount == 10) return "10 check-ins! Double digits! 🎯";
        if (checkInCount == 20) return "20 check-ins! Goal complete! 🏆";
        if (streak == 7)        return "7-day streak! A full week of consistency 🔥";
        if (streak == 30)       return "30-day streak! You're unstoppable 🌟";
        return null;
    }
}