package org.chaosmaker;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.server.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrontendListener {
    private static final System.Logger LOG = System.getLogger(FrontendListener.class.getName());

    private final int port;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Dispatcher dispatcher;
    private volatile boolean running = true;

    public FrontendListener(int port, Dispatcher dispatcher) {
        this.port = port;
        this.dispatcher = dispatcher;
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOG.log(System.Logger.Level.INFO, "Listening on port {0,number,#}", port);

            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    executor.submit(() -> ClientConnection.of(client, dispatcher).handle());
                } catch (IOException e) {
                    LOG.log(System.Logger.Level.ERROR, "Failed to accept connection", e);
                }
            }
        }
    }

    public void stop() {
        running = false;
        executor.shutdown();
    }
}
