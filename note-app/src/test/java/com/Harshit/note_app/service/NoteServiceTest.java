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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteService noteService;

    private User currentUser;
    private Note note;
    private NoteRequestDTO requestDTO;
    private NoteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("jane@example.com");

        note = new Note();
        note.setId(10L);
        note.setTitle("Title");
        note.setContent("Content");

        requestDTO = new NoteRequestDTO();
        requestDTO.setTitle("Title");
        requestDTO.setContent("Content");
        responseDTO = new NoteResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setTitle("Title");
        responseDTO.setContent("Content");
    }

    @Test
    void createNote_assignsCurrentUserAndReturnsDto() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(noteMapper.toEntity(requestDTO)).thenReturn(note);
        when(noteRepository.save(note)).thenReturn(note);
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        NoteResponseDTO result = noteService.createNote(requestDTO);

        assertEquals(responseDTO, result);
        assertEquals(currentUser, note.getUser());
        verify(noteRepository).save(note);
    }

    @Test
    void getAllNotes_withoutKeyword_returnsPagedDtosForCurrentUser() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        Page<Note> page = new PageImpl<>(List.of(note));
        when(noteRepository.findByUser(eq(currentUser), any(Pageable.class))).thenReturn(page);
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        Page<NoteResponseDTO> result = noteService.getAllNotes(Optional.empty(), 0, 10, "updatedAt");

        assertEquals(1, result.getTotalElements());
        verify(noteRepository).findByUser(eq(currentUser), any(Pageable.class));
        verify(noteRepository, never()).searchNotesByUser(any(), any(), any());
    }

    @Test
    void getAllNotes_withKeyword_searchesWithinCurrentUserNotes() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        Page<Note> page = new PageImpl<>(List.of(note));
        when(noteRepository.searchNotesByUser(eq(currentUser), eq("spring"), any(Pageable.class)))
                .thenReturn(page);
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        Page<NoteResponseDTO> result = noteService.getAllNotes(Optional.of("spring"), 1, 5, "title");

        assertEquals(1, result.getTotalElements());
        verify(noteRepository).searchNotesByUser(eq(currentUser), eq("spring"), any(Pageable.class));
    }

    @Test
    void getAllNotes_withInvalidSortField_throwsException() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);

        assertThrows(InvalidSortFieldException.class,
                () -> noteService.getAllNotes(Optional.empty(), 0, 10, "invalidField"));
    }

    @Test
    void getNoteById_returnsDtoWhenOwnedByCurrentUser() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(noteRepository.findByIdAndUser(10L, currentUser)).thenReturn(Optional.of(note));
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        NoteResponseDTO result = noteService.getNoteById(10L);

        assertEquals(responseDTO, result);
    }

    @Test
    void getNoteById_throwsWhenNoteNotFoundOrNotOwned() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(noteRepository.findByIdAndUser(99L, currentUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noteService.getNoteById(99L));
    }

    @Test
    void updateNote_updatesFieldsWhenNoteExists() {
        NoteRequestDTO updateRequest = new NoteRequestDTO();
        updateRequest.setTitle("New title");
        updateRequest.setContent("New content");
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(noteRepository.findByIdAndUser(10L, currentUser)).thenReturn(Optional.of(note));
        when(noteRepository.save(note)).thenReturn(note);
        when(noteMapper.toResponseDTO(note)).thenReturn(responseDTO);

        noteService.updateNote(10L, updateRequest);

        assertEquals("New title", note.getTitle());
        assertEquals("New content", note.getContent());
        verify(noteRepository).save(note);
    }

    @Test
    void deleteNote_deletesWhenNoteExists() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(noteRepository.findByIdAndUser(10L, currentUser)).thenReturn(Optional.of(note));

        noteService.deleteNote(10L);

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).delete(captor.capture());
        assertEquals(10L, captor.getValue().getId());
    }

    @Test
    void deleteNote_throwsWhenNoteNotFoundOrNotOwned() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(noteRepository.findByIdAndUser(99L, currentUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noteService.deleteNote(99L));

        verify(noteRepository, never()).delete(any());
    }
}
