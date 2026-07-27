package com.urbanradius.user.service;

import com.urbanradius.common.exception.UrbanRadiusException;
import com.urbanradius.user.dto.RateUserRequest;
import com.urbanradius.user.dto.RegisterUserRequest;
import com.urbanradius.user.dto.UserResponse;
import com.urbanradius.user.model.UserProfile;
import com.urbanradius.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request, String keycloakId, String email) {
        if (userRepository.existsByKeycloakId(keycloakId)) {
            throw new UrbanRadiusException(
                    "PROFILE_ALREADY_EXISTS",
                    "Profile already exists for this account",
                    409
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new UrbanRadiusException(
                    "DUPLICATE_EMAIL",
                    "Email already registered: " + email,
                    409
            );
        }

        UserProfile profile = new UserProfile(
                keycloakId,
                email,
                request.fullName(),
                request.phone(),
                request.city(),
                request.role()
        );

        return UserResponse.from(userRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(String keycloakId) {
        return UserResponse.from(findByKeycloakIdOrThrow(keycloakId));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return UserResponse.from(findProfileOrThrow(id));
    }

    @Transactional
    public UserResponse rateUser(UUID userId, RateUserRequest request, String keycloakId) {
        UserProfile rater = findByKeycloakIdOrThrow(keycloakId);

        if (userId.equals(rater.getId())) {
            throw new UrbanRadiusException(
                    "SELF_RATING_NOT_ALLOWED",
                    "Users cannot rate themselves",
                    400
            );
        }

        UserProfile profile = findProfileOrThrow(userId);
        profile.applyRating(request.score());
        return UserResponse.from(userRepository.save(profile));
    }

    private UserProfile findProfileOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UrbanRadiusException(
                        "USER_NOT_FOUND",
                        "User not found: " + id,
                        404
                ));
    }

    private UserProfile findByKeycloakIdOrThrow(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UrbanRadiusException(
                        "PROFILE_NOT_FOUND",
                        "Profile not found for authenticated user",
                        404
                ));
    }
}
