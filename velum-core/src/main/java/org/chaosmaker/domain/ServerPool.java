package org.chaosmaker.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerPool {
    private final List<Server> servers = new CopyOnWriteArrayList<>();

    public void addServer(Server server) {
        servers.add(server);
    }

    public void removeServer(String id) {
        servers.removeIf(s -> s.getId().equals(id));
    }

    public List<Server> getAllServers() {
        return List.copyOf(servers);
    }

    public List<Server> getHealthyServers() {
        return servers.stream().filter(Server::isAlive).toList();
    }
}
