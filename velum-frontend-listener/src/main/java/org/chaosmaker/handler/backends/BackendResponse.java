package org.chaosmaker.handler.backends;

import org.chaosmaker.domain.Server;

public record BackendResponse(
        String id,
        String host,
        int port,
        boolean alive,
        int activeConnections
) {
    public static BackendResponse from(Server server) {
        return new BackendResponse(
                server.getId(),
                server.getHost(),
                server.getPort(),
                server.isAlive(),
                server.getActiveConnections()
        );
    }
}
