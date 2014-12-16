package com.smilemeback.storage;

/**
 * Base class for Storage-related exceptions.
 */
public class StorageException extends Exception {
    public StorageException() { super(); }
    public StorageException(String message) { super(message); }
    public StorageException(String message, Throwable cause) { super(message, cause); }
    public StorageException(Throwable cause) { super(cause); }
}