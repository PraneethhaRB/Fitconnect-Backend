package com.fitconnect.fitconnect_backend.websocket;

import com.fitconnect.fitconnect_backend.entity.MembershipStatus;
import com.fitconnect.fitconnect_backend.entity.User;
import com.fitconnect.fitconnect_backend.repository.MembershipRepository;
import com.fitconnect.fitconnect_backend.repository.UserRepository;
import com.fitconnect.fitconnect_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination(); // e.g. "/topic/community/3"
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new SecurityException("Missing auth token for subscription");
            }
            token = token.substring(7);

            if (!jwtService.isTokenValid(token)) {
                throw new SecurityException("Invalid token");
            }

            String email = jwtService.extractEmail(token);
            Long communityId = extractCommunityId(destination);

            if (communityId != null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new SecurityException("User not found"));

                boolean isApprovedMember = membershipRepository
                        .findByUserIdAndCommunityId(user.getId(), communityId)
                        .map(m -> m.getStatus() == MembershipStatus.APPROVED)
                        .orElse(false);

                if (!isApprovedMember) {
                    throw new SecurityException("Not an approved member of this community");
                }
            }
        }

        return message;
    }

    private Long extractCommunityId(String destination) {
        if (destination == null) return null;
        String[] parts = destination.split("/");
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}