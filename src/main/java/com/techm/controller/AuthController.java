package com.techm.controller;

import com.techm.dto.LoginRequest;
import com.techm.dto.SignupRequest;
import com.techm.dto.JwtResponse;
import com.techm.dto.MessageResponse;
import com.techm.model.User;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("=== SIGNIN REQUEST ===");
        System.out.println("Username: " + loginRequest.getUsername());

        try {
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

            if (user == null) {
                System.out.println("User not found: " + loginRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: User not found!"));
            }

            // Simple password check
            String hashedPassword = hashPassword(loginRequest.getPassword());
            if (!user.getPasswordHash().equals(hashedPassword)) {
                System.out.println("Invalid password for user: " + loginRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Invalid credentials!"));
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate simple token
            String token = generateSimpleToken(user);
            System.out.println("Login successful for user: " + user.getUsername() + ", token: " + token);

            return ResponseEntity.ok(new JwtResponse(token,
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail()));
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Login failed! " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        System.out.println("=== SIGNUP REQUEST ===");
        System.out.println("Username: " + signUpRequest.getUsername());
        System.out.println("Email: " + signUpRequest.getEmail());

        try {
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                System.out.println("Username already exists: " + signUpRequest.getUsername());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                System.out.println("Email already exists: " + signUpRequest.getEmail());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
            }

            // Create new user
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPasswordHash(hashPassword(signUpRequest.getPassword()));
            user.setThemePreference("dark");
            user.setLastLogin(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            System.out.println("User created successfully: " + savedUser.getUsername() + " (ID: " + savedUser.getUserId() + ")");

            // Generate simple token
            String token = generateSimpleToken(savedUser);

            return ResponseEntity.ok(new JwtResponse(token,
                    savedUser.getUserId(),
                    savedUser.getUsername(),
                    savedUser.getEmail()));
        } catch (Exception e) {
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Registration failed! " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        System.out.println("=== TOKEN VALIDATION ===");
        System.out.println("Token received: " + token);

        try {
            String actualToken = token.substring(7); // Remove "Bearer " prefix
            Integer userId = validateSimpleToken(actualToken);

            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    System.out.println("Token validation successful for user: " + user.getUsername());
                    return ResponseEntity.ok(new JwtResponse(actualToken,
                            user.getUserId(),
                            user.getUsername(),
                            user.getEmail()));
                }
            }
            System.out.println("Token validation failed");
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
        }
    }

    // Simple password hashing (use BCrypt in production)
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Simple token generation
    private String generateSimpleToken(User user) {
        return "simple_" + user.getUserId() + "_" + System.currentTimeMillis();
    }

    // Simple token validation
    private Integer validateSimpleToken(String token) {
        try {
            if (token.startsWith("simple_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    return Integer.parseInt(parts[1]);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}





//package com.techm.controller;
//
//import com.techm.dto.LoginRequest;
//import com.techm.dto.SignupRequest;
//import com.techm.dto.JwtResponse;
//import com.techm.dto.MessageResponse;
//import com.techm.model.User;
//import com.techm.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.security.MessageDigest;
//import java.nio.charset.StandardCharsets;
//
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @PostMapping("/signin")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
//        try {
//            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
//
//            if (user == null) {
//                return ResponseEntity.badRequest()
//                        .body(new MessageResponse("Error: User not found!"));
//            }
//
//            // Simple password check (you can enhance this)
//            String hashedPassword = hashPassword(loginRequest.getPassword());
//            if (!user.getPasswordHash().equals(hashedPassword)) {
//                return ResponseEntity.badRequest()
//                        .body(new MessageResponse("Error: Invalid credentials!"));
//            }
//
//            // Update last login
//            user.setLastLogin(LocalDateTime.now());
//            userRepository.save(user);
//
//            // Generate simple token (user ID + timestamp)
//            String token = generateSimpleToken(user);
//
//            return ResponseEntity.ok(new JwtResponse(token,
//                    user.getUserId(),
//                    user.getUsername(),
//                    user.getEmail()));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new MessageResponse("Error: Login failed!"));
//        }
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
//        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            return ResponseEntity.badRequest()
//                    .body(new MessageResponse("Error: Username is already taken!"));
//        }
//
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            return ResponseEntity.badRequest()
//                    .body(new MessageResponse("Error: Email is already in use!"));
//        }
//
//        // Create new user
//        User user = new User();
//        user.setUsername(signUpRequest.getUsername());
//        user.setEmail(signUpRequest.getEmail());
//        user.setPasswordHash(hashPassword(signUpRequest.getPassword()));
//        user.setThemePreference("dark");
//        user.setLastLogin(LocalDateTime.now());
//
//        userRepository.save(user);
//
//        // Generate simple token
//        String token = generateSimpleToken(user);
//
//        return ResponseEntity.ok(new JwtResponse(token,
//                user.getUserId(),
//                user.getUsername(),
//                user.getEmail()));
//    }
//
//    @PostMapping("/validate")
//    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
//        try {
//            String actualToken = token.substring(7); // Remove "Bearer " prefix
//            Integer userId = validateSimpleToken(actualToken);
//
//            if (userId != null) {
//                User user = userRepository.findById(userId).orElse(null);
//                if (user != null) {
//                    return ResponseEntity.ok(new JwtResponse(actualToken,
//                            user.getUserId(),
//                            user.getUsername(),
//                            user.getEmail()));
//                }
//            }
//            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
//        }
//    }
//
//    // Simple password hashing (use BCrypt in production)
//    private String hashPassword(String password) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (Exception e) {
//            throw new RuntimeException("Error hashing password");
//        }
//    }
//
//    // Simple token generation
//    private String generateSimpleToken(User user) {
//        return "simple_" + user.getUserId() + "_" + System.currentTimeMillis();
//    }
//
//    // Simple token validation
//    private Integer validateSimpleToken(String token) {
//        try {
//            if (token.startsWith("simple_")) {
//                String[] parts = token.split("_");
//                if (parts.length >= 3) {
//                    return Integer.parseInt(parts[1]);
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}