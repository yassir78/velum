package org.chaosmaker.handler.register;

import java.util.ArrayList;
import java.util.List;

public class BackendRegistrationValidator {
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    public List<String> validate(BackendRegistrationRequest registration) {
        List<String> errors = new ArrayList<>();

        if (registration == null) {
            errors.add("Body is required");
            return errors;
        }

        if (isBlank(registration.id())) {
            errors.add("id is required");
        }

        if (isBlank(registration.host())) {
            errors.add("host is required");
        }

        if (registration.port() == null) {
            errors.add("port is required");
        } else if (registration.port() < MIN_PORT || registration.port() > MAX_PORT) {
            errors.add("port must be between " + MIN_PORT + " and " + MAX_PORT + ": " + registration.port());
        }

        return errors;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
