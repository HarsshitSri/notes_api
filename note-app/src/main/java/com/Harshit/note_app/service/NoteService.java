package com.Harshit.note_app.service;

import com.Harshit.note_app.dto.NoteRequestDTO;
import com.Harshit.note_app.dto.NoteResponseDTO;
import com.Harshit.note_app.exception.InvalidSortFieldException;
import com.Harshit.note_app.exception.ResourceNotFoundException;
import com.Harshit.note_app.mapper.NoteMapper;
import com.Harshit.note_app.model.Note;
import com.Harshit.note_app.model.User;
import com.Harshit.note_app.repository.NoteRepository;
import com.Harshit.note_app.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class NoteService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title", "createdAt", "updatedAt");

    private final NoteRepository noteRepository;
    private final SecurityUtils securityUtils;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, SecurityUtils securityUtils, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.securityUtils = securityUtils;
        this.noteMapper = noteMapper;
    }

    public NoteResponseDTO createNote(NoteRequestDTO requestDTO) {
        User currentUser = securityUtils.getCurrentUser();
        Note note = noteMapper.toEntity(requestDTO);
        note.setUser(currentUser);
        return noteMapper.toResponseDTO(noteRepository.save(note));
    }

    public Page<NoteResponseDTO> getAllNotes(Optional<String> keyword, int page, int size, String sortBy) {
        User currentUser = securityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(validateSortBy(sortBy)).descending());

        Page<Note> notes = keyword
                .filter(k -> !k.isBlank())
                .map(k -> noteRepository.searchNotesByUser(currentUser, k, pageable))
                .orElseGet(() -> noteRepository.findByUser(currentUser, pageable));

        return notes.map(noteMapper::toResponseDTO);
    }

    public NoteResponseDTO getNoteById(Long id) {
        return noteMapper.toResponseDTO(findOwnedNote(id));
    }

    public NoteResponseDTO updateNote(Long id, NoteRequestDTO requestDTO) {
        Note note = findOwnedNote(id);
        note.setTitle(requestDTO.getTitle());
        note.setContent(requestDTO.getContent());
        return noteMapper.toResponseDTO(noteRepository.save(note));
    }

    public void deleteNote(Long id) {
        noteRepository.delete(findOwnedNote(id));
    }

    private Note findOwnedNote(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        return noteRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + id));
    }

    private String validateSortBy(String sortBy) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new InvalidSortFieldException(sortBy);
        }
        return sortBy;
    }
}
