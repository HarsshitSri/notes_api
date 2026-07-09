package com.Harshit.note_app.mapper;

import com.Harshit.note_app.model.User;
import com.Harshit.note_app.dto.UserRegisterRequestDTO;
import com.Harshit.note_app.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegisterRequestDTO requestDTO) {
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setCreatedAt(user.getCreatedAt());
        return responseDTO;
    }
}
