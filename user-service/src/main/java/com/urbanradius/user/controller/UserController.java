package com.urbanradius.user.controller;

import com.urbanradius.common.exception.UrbanRadiusException;
import com.urbanradius.user.dto.RateUserRequest;
import com.urbanradius.user.dto.RegisterUserRequest;
import com.urbanradius.user.dto.UserResponse;
import com.urbanradius.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return userService.register(request, jwt.getSubject(), requireEmail(jwt));
    }

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal Jwt jwt) {
        return userService.getMe(jwt.getSubject());
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @PostMapping("/{id}/rate")
    public UserResponse rateUser(
            @PathVariable UUID id,
            @Valid @RequestBody RateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return userService.rateUser(id, request, jwt.getSubject());
    }

    private String requireEmail(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new UrbanRadiusException(
                    "EMAIL_CLAIM_MISSING",
                    "JWT must include email claim",
                    400
            );
        }
        return email;
    }
}
