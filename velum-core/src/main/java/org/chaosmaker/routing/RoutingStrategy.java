package org.chaosmaker.routing;

import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;

import java.util.Optional;

public interface RoutingStrategy {
    Optional<Server> pickServer();
}
