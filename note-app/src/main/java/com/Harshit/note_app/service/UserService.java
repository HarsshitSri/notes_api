package com.Harshit.note_app.service;

import com.Harshit.note_app.exception.DuplicateEmailException;
import com.Harshit.note_app.exception.DuplicateUsernameException;
import com.Harshit.note_app.model.User;
import com.Harshit.note_app.repository.UserRepository;
import com.Harshit.note_app.dto.UserLoginRequestDTO;
import com.Harshit.note_app.dto.UserLoginResponseDTO;
import com.Harshit.note_app.dto.UserRegisterRequestDTO;
import com.Harshit.note_app.dto.UserResponseDTO;
import com.Harshit.note_app.mapper.UserMapper;
import com.Harshit.note_app.security.CustomUserDetails;
import com.Harshit.note_app.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponseDTO register(UserRegisterRequestDTO requestDTO) {
        String email = normalizeEmail(requestDTO.getEmail());
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new DuplicateUsernameException();
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        User user = userMapper.toEntity(requestDTO);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public UserLoginResponseDTO login(UserLoginRequestDTO requestDTO) {
        String email = normalizeEmail(requestDTO.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new UserLoginResponseDTO(token, user.getUsername(), user.getEmail());
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
