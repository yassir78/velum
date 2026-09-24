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
    private static final System.Logger LOG = System.getLogger(HealthChecker.class.getName());
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
            LOG.log(System.Logger.Level.DEBUG, "Backend {0} ({1}) is healthy", server.getId(), server.getAddress());
        } catch (IOException e) {
            if (server.markDown()) {
                LOG.log(System.Logger.Level.WARNING, "Backend {0} ({1}) marked down: {2}",
                        server.getId(), server.getAddress(), e.getMessage());
            }
        }
    }


    public void stop() {
        scheduledExecutorService.shutdown();
    }

}
