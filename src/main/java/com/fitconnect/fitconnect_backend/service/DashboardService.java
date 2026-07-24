// package com.fitconnect.fitconnect_backend.service;

// import com.fitconnect.fitconnect_backend.dto.request.GoalUpdateRequest;
// import com.fitconnect.fitconnect_backend.dto.response.*;
// import com.fitconnect.fitconnect_backend.entity.*;
// import com.fitconnect.fitconnect_backend.repository.*;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Set;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// public class DashboardService {

//     private final UserRepository userRepository;
//     private final CommunityRepository communityRepository;
//     private final MembershipRepository membershipRepository;
//     private final LabOfferRepository labOfferRepository;

//     public DashboardResponse getDashboard(String email, Double userLat, Double userLng) {
//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//                 List<CommunityResponse> pending = membershipRepository
//                 .findByUserIdAndStatus(user.getId(), MembershipStatus.PENDING).stream()
//                 .map(m -> toCommunityResponse(m.getCommunity(), "PENDING", user.getId()))
//                 .collect(Collectors.toList());
//         List<Membership> approvedMemberships =
//                 membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.APPROVED);

//         List<CommunityResponse> joined = approvedMemberships.stream()
//         .map(m -> toCommunityResponse(m.getCommunity(), "APPROVED", user.getId()))
//                 .collect(Collectors.toList());

//         Set<Long> excludedCommunityIds = approvedMemberships.stream()
//                 .map(m -> m.getCommunity().getId())
//                 .collect(Collectors.toSet());
//         excludedCommunityIds.addAll(
//                 membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.PENDING).stream()
//                         .map(m -> m.getCommunity().getId())
//                         .collect(Collectors.toSet())
//         );

//         // List<CommunityResponse> recommended = communityRepository.findAll().stream()
//         //         .filter(c -> !excludedCommunityIds.contains(c.getId()))
//         //         .map(c -> toCommunityResponse(c, "NOT_JOINED", user.getId()))
//         //         .collect(Collectors.toList());
//         String preferredFocus = goalCategoryToFocus(user.getGoalCategory());

// List<CommunityResponse> recommended = communityRepository.findAll().stream()
//     .filter(c -> !excludedCommunityIds.contains(c.getId()))
//     .sorted((a, b) -> {
//         boolean aMatches = preferredFocus != null && preferredFocus.equals(a.getGoalFocus());
//         boolean bMatches = preferredFocus != null && preferredFocus.equals(b.getGoalFocus());
//         if (aMatches && !bMatches) return -1;
//         if (!aMatches && bMatches) return 1;
//         return 0;
//     })
//     .map(c -> toCommunityResponse(c, "NOT_JOINED", user.getId()))
//     .collect(Collectors.toList());

//                 List<LabOffer> allOffers = labOfferRepository.findAll();

//                 List<LabOfferResponse> labOffers = allOffers.stream()
//                         .map(l -> {
//                             String distance = (userLat != null && userLng != null && l.getLatitude() != null)
//                                     ? formatDistance(haversine(userLat, userLng, l.getLatitude(), l.getLongitude()))
//                                     : l.getDistance(); // fallback to static seeded text
//                             return new LabOfferResponse(l.getId(), l.getLabName(), distance,
//                                     l.getOfferText(), l.getTestType(), l.getValidUntil());
//                         })
//                         .sorted((a, b) -> { /* optional: sort by parsed distance */ return 0; })
//                         .collect(Collectors.toList());

//         UserProfileResponse profile =new UserProfileResponse(
//                 user.getId(), user.getName(), user.getEmail(),
//                 user.getAvatarColor(), user.getGoalText(), user.getGoalProgress(),
//                 user.getCheckInCount() != null ? user.getCheckInCount() : 0,
//                 user.getLastCheckInAt(),
//                 user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
//                 user.getLongestStreak() != null ? user.getLongestStreak() : 0
//             );

//     WeatherService.WorkoutSuggestion suggestion =
//     weatherService.getSuggestion(userLat, userLng, user.getGoalCategory());

// return new DashboardResponse(
//     profile, joined, pending, recommended, labOffers, suggestion
// );

       
//     }

//     private double haversine(double lat1, double lon1, double lat2, double lon2) {
//         double R = 6371; // earth radius in km
//         double dLat = Math.toRadians(lat2 - lat1);
//         double dLon = Math.toRadians(lon2 - lon1);
//         double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
//         double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//         return R * c;
//     }
    
//     private String formatDistance(double km) {
//         return km < 1 ? Math.round(km * 1000) + " m" : String.format("%.1f km", km);
//     }
//     public UserProfileResponse updateGoal(String email, GoalUpdateRequest request) {
//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
    
//         if (request.getGoalText() != null) {
//             user.setGoalText(request.getGoalText());
//         }
//         // goalProgress is no longer manually settable — it's derived from check-ins
    
//         User saved = userRepository.save(user);
    
//         return new UserProfileResponse(
//                 user.getId(), user.getName(), user.getEmail(),
//                 user.getAvatarColor(), user.getGoalText(), user.getGoalProgress(),
//                 user.getCheckInCount() != null ? user.getCheckInCount() : 0,
//                 user.getLastCheckInAt(),
//                 user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
//                 user.getLongestStreak() != null ? user.getLongestStreak() : 0
//             );
//     }

//     private CommunityResponse toCommunityResponse(Communityy c, String status, Long currentUserId) {
//         long memberCount = membershipRepository.countByCommunityIdAndStatus(c.getId(), MembershipStatus.APPROVED);
//         boolean isAdmin = c.getAdmin().getId().equals(currentUserId);
//         return new CommunityResponse(
//                 c.getId(), c.getName(), c.getDescription(), c.getGoalFocus(),
//                 c.getCoverColor(), memberCount, status, isAdmin);
//     }
//     private String goalCategoryToFocus(GoalCategory category) {
//         if (category == null) return null;
//         return switch (category) {
//             case WEIGHT_LOSS -> "LOSE_WEIGHT";
//             case MUSCLE_GAIN -> "BUILD_MUSCLE";
//             case ENDURANCE, FLEXIBILITY -> "HEALTHY_LIFESTYLE";
//             default -> null;
//         };
//     }
// }
package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.dto.request.GoalUpdateRequest;
import com.fitconnect.fitconnect_backend.dto.response.*;
import com.fitconnect.fitconnect_backend.entity.*;
import com.fitconnect.fitconnect_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final MembershipRepository membershipRepository;
    private final LabOfferRepository labOfferRepository;
    private final WeatherService weatherService; // was missing — used below but never declared

    public DashboardResponse getDashboard(String email, Double userLat, Double userLng) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CommunityResponse> pending = membershipRepository
                .findByUserIdAndStatus(user.getId(), MembershipStatus.PENDING).stream()
                .map(m -> toCommunityResponse(m.getCommunity(), "PENDING", user.getId()))
                .collect(Collectors.toList());

        List<Membership> approvedMemberships =
                membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.APPROVED);

        List<CommunityResponse> joined = approvedMemberships.stream()
                .map(m -> toCommunityResponse(m.getCommunity(), "APPROVED", user.getId()))
                .collect(Collectors.toList());

        Set<Long> excludedCommunityIds = approvedMemberships.stream()
                .map(m -> m.getCommunity().getId())
                .collect(Collectors.toSet());
        excludedCommunityIds.addAll(
                membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.PENDING).stream()
                        .map(m -> m.getCommunity().getId())
                        .collect(Collectors.toSet())
        );

        String preferredFocus = goalCategoryToFocus(user.getGoalCategory());

        List<CommunityResponse> recommended = communityRepository.findAll().stream()
                .filter(c -> !excludedCommunityIds.contains(c.getId()))
                .sorted((a, b) -> {
                    boolean aMatches = preferredFocus != null && preferredFocus.equals(a.getGoalFocus());
                    boolean bMatches = preferredFocus != null && preferredFocus.equals(b.getGoalFocus());
                    if (aMatches && !bMatches) return -1;
                    if (!aMatches && bMatches) return 1;
                    return 0;
                })
                .map(c -> toCommunityResponse(c, "NOT_JOINED", user.getId()))
                .collect(Collectors.toList());

        List<LabOffer> allOffers = labOfferRepository.findAll();

        List<LabOfferResponse> labOffers = allOffers.stream()
                .map(l -> {
                    String distance = (userLat != null && userLng != null && l.getLatitude() != null)
                            ? formatDistance(haversine(userLat, userLng, l.getLatitude(), l.getLongitude()))
                            : l.getDistance(); // fallback to static seeded text
                    return new LabOfferResponse(l.getId(), l.getLabName(), distance,
                            l.getOfferText(), l.getTestType(), l.getValidUntil());
                })
                // NOTE: this comparator is a no-op (always returns 0), so offers aren't
                // actually reordered. If you want real distance sorting, compute the
                // haversine distance as a double BEFORE mapping to LabOfferResponse,
                // sort on that, then map to the response DTO.
                .sorted((a, b) -> 0)
                .collect(Collectors.toList());

        UserProfileResponse profile = new UserProfileResponse(
                user.getId(), user.getName(), user.getEmail(),
                user.getAvatarColor(), user.getGoalText(), user.getGoalProgress(),
                user.getCheckInCount() != null ? user.getCheckInCount() : 0,
                user.getLastCheckInAt(),
                user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
                user.getLongestStreak() != null ? user.getLongestStreak() : 0
        );

        WorkoutSuggestionResponse suggestion =
                weatherService.getSuggestion(userLat, userLng, user.getGoalCategory());

        return new DashboardResponse(
            profile, joined, recommended, labOffers, pending, suggestion
        );
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String formatDistance(double km) {
        return km < 1 ? Math.round(km * 1000) + " m" : String.format("%.1f km", km);
    }

    public UserProfileResponse updateGoal(String email, GoalUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getGoalText() != null) {
            user.setGoalText(request.getGoalText());
        }
        // goalProgress is no longer manually settable — it's derived from check-ins

        User saved = userRepository.save(user);

        return new UserProfileResponse(
                saved.getId(), saved.getName(), saved.getEmail(),
                saved.getAvatarColor(), saved.getGoalText(), saved.getGoalProgress(),
                saved.getCheckInCount() != null ? saved.getCheckInCount() : 0,
                saved.getLastCheckInAt(),
                saved.getCurrentStreak() != null ? saved.getCurrentStreak() : 0,
                saved.getLongestStreak() != null ? saved.getLongestStreak() : 0
        );
    }

    private CommunityResponse toCommunityResponse(Communityy c, String status, Long currentUserId) {
        long memberCount = membershipRepository.countByCommunityIdAndStatus(c.getId(), MembershipStatus.APPROVED);
        boolean isAdmin = c.getAdmin().getId().equals(currentUserId);
        return new CommunityResponse(
                c.getId(), c.getName(), c.getDescription(), c.getGoalFocus(),
                c.getCoverColor(), memberCount, status, isAdmin);
    }

    private String goalCategoryToFocus(GoalCategory category) {
        if (category == null) return null;
        return switch (category) {
            case WEIGHT_LOSS -> "LOSE_WEIGHT";
            case MUSCLE_GAIN -> "BUILD_MUSCLE";
            case ENDURANCE, FLEXIBILITY -> "HEALTHY_LIFESTYLE";
            default -> null;
        };
    }
}