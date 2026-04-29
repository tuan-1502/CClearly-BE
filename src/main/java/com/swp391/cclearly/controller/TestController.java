package com.swp391.cclearly.controller;

import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Temporary controller for testing - DELETE AFTER USE
 */
@RestController
@RequestMapping("/api/public/test")
@RequiredArgsConstructor
public class TestController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping("/hash")
    public String generateHash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }

    @GetMapping("/verify")
    public boolean verifyHash(@RequestParam String password, @RequestParam String hash) {
        return passwordEncoder.matches(password, hash);
    }

    @GetMapping("/check-user")
    public Map<String, Object> checkUser(@RequestParam String email, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            result.put("exists", false);
            result.put("message", "User not found");
            return result;
        }
        
        User user = userOpt.get();
        result.put("exists", true);
        result.put("email", user.getEmail());
        result.put("status", user.getStatus());
        result.put("isEmailVerified", user.getIsEmailVerified());
        result.put("role", user.getRole() != null ? user.getRole().getRoleName() : "null");
        result.put("passwordHashInDb", user.getPasswordHash());
        result.put("passwordMatches", passwordEncoder.matches(password, user.getPasswordHash()));
        
        return result;
    }
}
