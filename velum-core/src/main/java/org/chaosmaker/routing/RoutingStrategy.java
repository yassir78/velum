package org.chaosmaker.routing;

import org.chaosmaker.domain.Server;

import java.util.Optional;

public interface RoutingStrategy {
    Optional<Server> pickServer();
}
