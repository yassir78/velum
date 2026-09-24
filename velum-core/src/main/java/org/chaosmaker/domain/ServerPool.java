package org.chaosmaker.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServerPool {
    private final Set<Server> servers = new CopyOnWriteArraySet<>();

    private ServerPool() {
    }

    public static ServerPool getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ServerPool INSTANCE = new ServerPool();
    }

    public boolean addServerIfAbsent(Server server) {
        if (Objects.isNull(server) || Objects.isNull(server.getId())) return false;
        return servers.add(server);
    }

    public void removeServer(String id) {
        if (Objects.isNull(id)) return;
        servers.removeIf(s -> id.equals(s.getId()));
    }

    public List<Server> getAllServers() {
        return List.copyOf(servers);
    }

    public List<Server> getHealthyServers() {
        return servers.stream().filter(Server::isAlive).toList();
    }
}