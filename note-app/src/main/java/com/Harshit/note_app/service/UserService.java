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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponseDTO register(UserRegisterRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new DuplicateUsernameException();
        }
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException();
        }

        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public UserLoginResponseDTO login(UserLoginRequestDTO requestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String token = jwtService.generateToken(userDetails);

        return new UserLoginResponseDTO(token, user.getUsername(), user.getEmail());
    }
}
