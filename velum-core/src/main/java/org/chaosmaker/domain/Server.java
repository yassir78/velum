package org.chaosmaker.domain;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final String id;
    private final String host;
    private final int port;
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public Server(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String getId() { return id; }
    public String getHost() { return host; }
    public int getPort() { return port; }

    public boolean isAlive() { return alive.get(); }
    public void setAlive(boolean alive) { this.alive.set(alive); }

    public boolean markDown() {
        return alive.getAndSet(false);
    }

    public int getActiveConnections() { return activeConnections.get(); }
    public void incrementConnections() { activeConnections.incrementAndGet(); }
    public void decrementConnections() { activeConnections.decrementAndGet(); }

    public String getAddress() {
        return host + ":" + port;
    }

    public URI getUri() {
        return URI.create("http://" + getAddress());
    }
}
