package org.chaosmaker.routing;

public class NoAvailableServerException extends RuntimeException {
    public NoAvailableServerException(String message) {
        super(message);
    }
}
