package org.chaosmaker;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.server.ClientConnection;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrontendListener {

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
            System.out.println("[FrontendListener] Listening on port " + port);

            while (running) {
                Socket client = serverSocket.accept();
                executor.submit(() -> new ClientConnection(client,dispatcher).handle());
            }
        }
    }

    public void stop() {
        running = false;
        executor.shutdown();
    }
}
