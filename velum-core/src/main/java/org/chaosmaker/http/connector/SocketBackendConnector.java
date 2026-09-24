package org.chaosmaker.http.connector;

import org.chaosmaker.domain.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.http.HttpClient;

public class SocketBackendConnector implements BackendConnector {

    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 3_000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 10_000;

    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public SocketBackendConnector() {
        this(DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
    }

    public SocketBackendConnector(int connectTimeoutMs, int readTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    @Override
    public Socket connect(Server server) throws IOException {
        var socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(server.getHost(), server.getPort()), connectTimeoutMs);
            socket.setSoTimeout(readTimeoutMs);
            return socket;
        } catch (IOException e) {
            socket.close();
            throw e;
        }
    }
}
