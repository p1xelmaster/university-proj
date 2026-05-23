package kz.iitu.hello.web.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.security.JwtUtil;
import kz.iitu.hello.web.dto.auth.AuthResponse;
import kz.iitu.hello.web.dto.auth.ChangePasswordRequest;
import kz.iitu.hello.web.dto.auth.LoginRequest;
import kz.iitu.hello.web.dto.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, registration, and password management")
public class PanMaratAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = usersRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token, user.getRole(), user.getId());
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user account (assigned GUEST role)")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (usersRepository.findByUserName(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.GUEST);

        usersRepository.save(user);

        String token = jwtUtil.generateToken(user.getUserName());
        return new AuthResponse(token, user.getRole(), user.getId());
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for the currently authenticated user")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = usersRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is invalid");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
    }
}
