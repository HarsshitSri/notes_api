package com.Harshit.note_app.controller;

import com.Harshit.note_app.config.OpenApiConfig;
import com.Harshit.note_app.dto.ApiErrorResponse;
import com.Harshit.note_app.dto.NoteRequestDTO;
import com.Harshit.note_app.dto.NoteResponseDTO;
import com.Harshit.note_app.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "Create, read, update, and delete personal notes. All operations are scoped to the authenticated user.")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "Create a note", description = "Creates a new note owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Note created successfully",
                    content = @Content(schema = @Schema(implementation = NoteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@Valid @RequestBody NoteRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(requestDTO));
    }

    @Operation(summary = "List notes", description = "Returns a paginated list of the authenticated user's notes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sort field",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<NoteResponseDTO>> getNotes(
            @Parameter(description = "Search keyword matched against title and content")
            @RequestParam Optional<String> search,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of notes per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field: title, createdAt, or updatedAt")
            @RequestParam(defaultValue = "updatedAt") String sortBy
    ) {
        return ResponseEntity.ok(noteService.getAllNotes(search, page, size, sortBy));
    }

    @Operation(summary = "Get a note by ID", description = "Returns a single note owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note found",
                    content = @Content(schema = @Schema(implementation = NoteResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @Operation(summary = "Update a note", description = "Updates a note owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note updated successfully",
                    content = @Content(schema = @Schema(implementation = NoteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(noteService.updateNote(id, requestDTO));
    }

    @Operation(summary = "Delete a note", description = "Deletes a note owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Note deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
