package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.dto.request.*;
import com.fitconnect.fitconnect_backend.dto.response.*;
// import com.fitconnect.dto.request.SignupRequest;
// import com.fitconnect.dto.response.AuthResponse;
// import com.fitconnect.dto.response.UserProfileResponse;
import com.fitconnect.fitconnect_backend.entity.Role;
import com.fitconnect.fitconnect_backend.entity.User;
import com.fitconnect.fitconnect_backend.exception.DuplicateEmailException;
import com.fitconnect.fitconnect_backend.exception.InvalidCredentialsException;
import com.fitconnect.fitconnect_backend.repository.UserRepository;
import com.fitconnect.fitconnect_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final List<String> AVATAR_COLORS =
            List.of("#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", "#98D8C8");

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash before saving
        user.setGoalText(request.getGoal() != null ? request.getGoal() : "Set your fitness goal");
        user.setGoalProgress(0);
        user.setRole(Role.USER);
        user.setAvatarColor(AVATAR_COLORS.get((int) (Math.random() * AVATAR_COLORS.size())));

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponse(token, toProfile(saved));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, toProfile(user));
    }

    private UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarColor(),
                user.getGoalText(),
                user.getGoalProgress(),
                user.getCheckInCount(),
                user.getLastCheckInAt()
        );
    }
}