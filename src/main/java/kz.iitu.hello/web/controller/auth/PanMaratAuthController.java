package kz.iitu.hello.web.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.enums.UserRole;
import kz.iitu.hello.domain.repository.UsersRepository;
import kz.iitu.hello.security.JwtUtil;
import kz.iitu.hello.web.dto.auth.PanMaratAuthResponse;
import kz.iitu.hello.web.dto.auth.PanMaratChangePasswordRequest;
import kz.iitu.hello.web.dto.auth.PanMaratLoginRequest;
import kz.iitu.hello.web.dto.auth.PanMaratRegisterRequest;
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
    public PanMaratAuthResponse login(@RequestBody PanMaratLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        PanMaratUser user = usersRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PanMaratUser not found"));

        String token = jwtUtil.generateToken(request.getUsername());
        return new PanMaratAuthResponse(token, user.getRole(), user.getId());
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user account (assigned GUEST role)")
    public PanMaratAuthResponse register(@RequestBody PanMaratRegisterRequest request) {
        if (usersRepository.findByUserName(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        PanMaratUser user = new PanMaratUser();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.GUEST);

        usersRepository.save(user);

        String token = jwtUtil.generateToken(user.getUserName());
        return new PanMaratAuthResponse(token, user.getRole(), user.getId());
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for the currently authenticated user")
    public void changePassword(@Valid @RequestBody PanMaratChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        PanMaratUser user = usersRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PanMaratUser not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is invalid");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
    }
}
