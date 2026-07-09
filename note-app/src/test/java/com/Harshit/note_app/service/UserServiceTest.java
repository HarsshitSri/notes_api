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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesEncodedUserAndReturnsResponse() {
        // Verifies the happy path: unique username/email, password hashing, persistence, and DTO mapping.
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("jane", "jane@example.com", "secret12");
        User user = new User();
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("jane");
        savedUser.setEmail("jane@example.com");
        savedUser.setCreatedAt(LocalDateTime.now());
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "jane", "jane@example.com", savedUser.getCreatedAt());

        when(userRepository.existsByUsername("jane")).thenReturn(false);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("secret12")).thenReturn("hashed-password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponseDTO(savedUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.register(request);

        assertEquals(responseDTO, result);
        assertEquals("hashed-password", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void register_throwsWhenUsernameAlreadyExists() {
        // Verifies duplicate username is rejected before email is checked or user is saved.
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("jane", "jane@example.com", "secret12");
        when(userRepository.existsByUsername("jane")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> userService.register(request));

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        // Verifies duplicate email is rejected after username passes uniqueness check.
        UserRegisterRequestDTO request = new UserRegisterRequestDTO("jane", "jane@example.com", "secret12");
        when(userRepository.existsByUsername("jane")).thenReturn(false);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.register(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsTokenAndUserDetailsOnSuccess() {
        // Verifies successful authentication produces a JWT and login response with username and email.
        UserLoginRequestDTO request = new UserLoginRequestDTO("jane@example.com", "secret12");
        User user = new User();
        user.setUsername("jane");
        user.setEmail("jane@example.com");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        UserLoginResponseDTO response = userService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("jane", response.getUsername());
        assertEquals("jane@example.com", response.getEmail());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("jane@example.com", captor.getValue().getPrincipal());
        assertEquals("secret12", captor.getValue().getCredentials());
    }

    @Test
    void login_propagatesAuthenticationExceptionWhenCredentialsInvalid() {
        // Verifies invalid credentials bubble up as an AuthenticationException for the global handler.
        UserLoginRequestDTO request = new UserLoginRequestDTO("jane@example.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> userService.login(request));

        verify(jwtService, never()).generateToken(any());
    }
}
