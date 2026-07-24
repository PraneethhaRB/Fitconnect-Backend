package com.fitconnect.fitconnect_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitconnect.fitconnect_backend.dto.request.CreateCommunityRequest;
import com.fitconnect.fitconnect_backend.dto.response.CommunityResponse;
import com.fitconnect.fitconnect_backend.dto.response.LeaderboardEntryResponse;

import com.fitconnect.fitconnect_backend.dto.response.MembershipResponse;
import com.fitconnect.fitconnect_backend.entity.Communityy;
import com.fitconnect.fitconnect_backend.entity.Membership;
import com.fitconnect.fitconnect_backend.entity.MembershipStatus;
import com.fitconnect.fitconnect_backend.entity.User;
import com.fitconnect.fitconnect_backend.exception.ForbiddenActionException;
import com.fitconnect.fitconnect_backend.exception.ResourceNotFoundException;
import com.fitconnect.fitconnect_backend.repository.CommunityRepository;
import com.fitconnect.fitconnect_backend.repository.MembershipRepository;
import com.fitconnect.fitconnect_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CommunityService {

private  final CommunityRepository communityRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    public void requestToJoin(String requesterEmail, Long communityId) 
    {
      User user=  userRepository.findByEmail(requesterEmail).orElseThrow(()-> new ResourceNotFoundException("User not found"));
      Communityy communityy = communityRepository.findById(communityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
      if(membershipRepository.existsByUserIdAndCommunityId(user.getId(), communityy.getId()))
      {
        throw new IllegalArgumentException("User has already requested to join this community");
      }
      Membership membership = new Membership();
      membership.setUser(user);
      membership.setCommunity(communityy);
      membership.setStatus(MembershipStatus.PENDING);
      membershipRepository.save(membership);


    }
    public List<MembershipResponse> getPendingRequests(String adminEmail, Long communityId)
    {
        Communityy communityy = communityRepository.findById(communityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
        verifyIsAdmin(adminEmail, communityy);
        return membershipRepository.findByCommunityIdAndStatus(communityId, MembershipStatus.PENDING).stream().map(m-> new MembershipResponse(m.getId(), m.getUser().getId(), m.getUser().getName(), m.getUser().getAvatarColor(), m.getRequestedAt())).collect(Collectors.toList());


    }
    public void approveRequest(String adminEmail,Long CommunityId, Long membershipId)
    {
        Communityy communityy = communityRepository.findById(CommunityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
        verifyIsAdmin(adminEmail, communityy);
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(()-> new ResourceNotFoundException("Membership request not found"));
       if(!membership.getCommunity().getId().equals(communityy.getId()))
       {
        throw new ForbiddenActionException("This membership request does not belong to this community");
       }
        membership.setStatus(MembershipStatus.APPROVED);
        membershipRepository.save(membership);
    }
    public void rejectRequest(String adminEmail,Long communityId, Long membershipId)
    {
        Communityy communityy = communityRepository.findById(communityId).orElseThrow(()-> new ResourceNotFoundException("Community not found"));
        verifyIsAdmin(adminEmail, communityy);
        Membership membership = membershipRepository.findById(membershipId).orElseThrow(()-> new ResourceNotFoundException("Membership request not found"));
        if(!membership.getCommunity().getId().equals(communityy.getId()))
        {
            throw new ForbiddenActionException("This membership request does not belong to this community");
        }
        membership.setStatus(MembershipStatus.REJECTED);
        membershipRepository.save(membership);
    }
    private void verifyIsAdmin(String requesterEmail, Communityy community) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!community.getAdmin().getId().equals(requester.getId())) {
            throw new ForbiddenActionException("You are not the admin of this community");
        }
    }
    private static final List<String> COVER_COLORS =
        List.of("#009688", "#26A69A", "#80CBC4", "#43A047", "#FB8C00");

public CommunityResponse createCommunity(String creatorEmail, CreateCommunityRequest request) {
    User creator = userRepository.findByEmail(creatorEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Communityy community = new Communityy();
    community.setName(request.getName());
    community.setDescription(request.getDescription());
    community.setGoalFocus(request.getGoalFocus());
    community.setCoverColor(
            request.getCoverColor() != null ? request.getCoverColor()
                    : COVER_COLORS.get((int) (Math.random() * COVER_COLORS.size()))
    );
    community.setAdmin(creator);

    Communityy saved = communityRepository.save(community);

    // Creator is automatically an approved member of their own community
    Membership creatorMembership = new Membership();
    creatorMembership.setUser(creator);
    creatorMembership.setCommunity(saved);
    creatorMembership.setStatus(MembershipStatus.APPROVED);
    membershipRepository.save(creatorMembership);
    
    return toCommunityResponse(saved, "APPROVED", creator.getId());
    // "APPROVED" here is cosmetic — the creator is admin, not a regular member;
    // frontend just needs *some* status so the card renders sensibly
}
public List<LeaderboardEntryResponse> getLeaderboard(Long communityId) {
    communityRepository.findById(communityId)
            .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

    List<Membership> members = membershipRepository
            .findByCommunityIdAndStatus(communityId, MembershipStatus.APPROVED);

    List<LeaderboardEntryResponse> leaderboard = new java.util.ArrayList<>();

    for (int i = 0; i < members.size(); i++) {
        User user = members.get(i).getUser();
        leaderboard.add(new LeaderboardEntryResponse(
                user.getId(),
                user.getName(),
                user.getAvatarColor(),
                user.getCheckInCount() != null ? user.getCheckInCount() : 0,
                user.getCurrentStreak() != null ? user.getCurrentStreak() : 0,
                user.getGoalProgress() != null ? user.getGoalProgress() : 0,
                0 // rank assigned after sorting
        ));
    }

    // sort by check-in count descending
    leaderboard.sort((a, b) -> b.getCheckInCount() - a.getCheckInCount());

    // assign ranks after sorting
    for (int i = 0; i < leaderboard.size(); i++) {
        leaderboard.get(i).setRank(i + 1);
    }

    return leaderboard;
}
private CommunityResponse toCommunityResponse(Communityy c, String status, Long currentUserId) {
    long memberCount = membershipRepository.countByCommunityIdAndStatus(c.getId(), MembershipStatus.APPROVED);
    boolean isAdmin = c.getAdmin().getId().equals(currentUserId);
    return new CommunityResponse(
            c.getId(), c.getName(), c.getDescription(), c.getGoalFocus(),
            c.getCoverColor(), memberCount, status, isAdmin);
}

}
