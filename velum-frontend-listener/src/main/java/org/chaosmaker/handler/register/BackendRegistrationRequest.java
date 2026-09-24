package org.chaosmaker.handler.register;

public record BackendRegistrationRequest(
        String id,
        String host,
        Integer port
) {
}
