package com.Harshit.note_app.exception;

public class InvalidSortFieldException extends RuntimeException {

    public InvalidSortFieldException(String sortBy) {
        super("Invalid sort field: " + sortBy);
    }
}
