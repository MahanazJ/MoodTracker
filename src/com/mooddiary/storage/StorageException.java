package com.mooddiary.storage;

/** Unchecked wrapper for I/O and data-format failures in the storage layer. */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
