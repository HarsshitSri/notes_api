package com.Harshit.note_app.mapper;

import com.Harshit.note_app.dto.NoteRequestDTO;
import com.Harshit.note_app.dto.NoteResponseDTO;
import com.Harshit.note_app.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public Note toEntity(NoteRequestDTO requestDTO) {
        Note note = new Note();
        note.setTitle(requestDTO.getTitle());
        note.setContent(requestDTO.getContent());
        return note;
    }

    public NoteResponseDTO toResponseDTO(Note note) {
        NoteResponseDTO responseDTO = new NoteResponseDTO();
        responseDTO.setId(note.getId());
        responseDTO.setTitle(note.getTitle());
        responseDTO.setContent(note.getContent());
        responseDTO.setCreatedAt(note.getCreatedAt());
        responseDTO.setUpdatedAt(note.getUpdatedAt());
        return responseDTO;
    }
}
