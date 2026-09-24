package org.chaosmaker.health_check;

import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HealthChecker {
    private static final int INITIAL_DELAY = 5;
    private final ServerPool serverPool;
    private final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor();

    public HealthChecker(ServerPool pool) {
        this.serverPool = pool;
    }

    public void start(int time) {
        scheduledExecutorService
                .scheduleAtFixedRate(
                        () -> serverPool
                                .getAllServers()
                                .forEach(this::checkServer),
                        INITIAL_DELAY,
                        time,
                        TimeUnit.SECONDS
                );
    }

    // TODO : use an http client and check status
    private void checkServer(Server server) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(server.getHost(), server.getPort()), 2000);
            System.out.println("server is healthy");
        } catch (IOException e) {
            System.out.println("server is not healthy");
            server.markDown();
        }
    }


    public void stop() {
        scheduledExecutorService.shutdown();
    }

}
