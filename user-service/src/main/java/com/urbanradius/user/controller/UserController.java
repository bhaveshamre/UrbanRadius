package com.urbanradius.user.controller;

import com.urbanradius.user.dto.RateUserRequest;
import com.urbanradius.user.dto.RegisterUserRequest;
import com.urbanradius.user.dto.UserResponse;
import com.urbanradius.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
        return userService.register(request);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @PostMapping("/{id}/rate")
    public UserResponse rateUser(@PathVariable UUID id, @Valid @RequestBody RateUserRequest request) {
        return userService.rateUser(id, request);
    }
}
