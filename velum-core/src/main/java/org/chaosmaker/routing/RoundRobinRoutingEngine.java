package org.chaosmaker.routing;

import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;

import java.util.Optional;

public class RoundRobinRoutingEngine implements RoutingStrategy {
    private final ServerPool serverPool;

    public RoundRobinRoutingEngine(ServerPool serverPool) {
        this.serverPool = serverPool;
    }

    @Override
    public Optional<Server> pickServer() {
        // TODO: implement round robin algorithm
        return Optional.of(
                serverPool
                        .getAllServers()
                        .getFirst());
    }
}
