package org.chaosmaker.routing;

import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinRoutingEngine implements RoutingStrategy {
    private final ServerPool serverPool;
    private final AtomicInteger counter = new AtomicInteger(0);

    public RoundRobinRoutingEngine(ServerPool serverPool) {
        this.serverPool = serverPool;
    }

    @Override
    public Optional<Server> pickServer() {
        List<Server> healthyServers = serverPool.getHealthyServers();

        if (healthyServers.isEmpty()) {
            return Optional.empty();
        }

        int index = Math.floorMod(counter.getAndIncrement(), healthyServers.size());
        return Optional.of(healthyServers.get(index));
    }
}
