package com.fitconnect.fitconnect_backend.service;

import com.fitconnect.fitconnect_backend.entity.User;
import com.fitconnect.fitconnect_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreakResetService {

    private final UserRepository userRepository;

    // runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void resetExpiredStreaks() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
            if (user.getLastCheckInAt() == null) continue;

            LocalDate lastCheckIn = user.getLastCheckInAt().toLocalDate();
            long daysSinceLast = java.time.temporal.ChronoUnit.DAYS.between(lastCheckIn, today);

            if (daysSinceLast > 1 && user.getCurrentStreak() > 0) {
                user.setCurrentStreak(0);
                userRepository.save(user);
            }
        }
    }
}