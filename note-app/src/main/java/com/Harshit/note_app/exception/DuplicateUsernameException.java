package com.Harshit.note_app.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException() {
        super("Username is already taken");
    }
}
