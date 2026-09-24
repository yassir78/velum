package org.chaosmaker.http.client;

import org.chaosmaker.domain.Server;
import org.chaosmaker.http.connector.BackendConnector;
import org.chaosmaker.http.connector.SocketBackendConnector;
import org.chaosmaker.http.exception.ForwardingException;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.http.reader.HttpRequestReader;
import org.chaosmaker.http.writer.HttpResponseWriter;

import java.io.IOException;
import java.net.Socket;

public class HttpForwarder {

    private final BackendConnector connector = new SocketBackendConnector();
    private final HttpResponseWriter writer = new HttpResponseWriter();
    private final HttpRequestReader reader = new HttpRequestReader();

    public HttpResponse forward(HttpRequest request, Server server) {
        try (Socket socket = connector.connect(server)) {
            writer.writeRequest(socket.getOutputStream(), request, server.getAddress());
            return reader.readResponse(socket.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
            throw new ForwardingException(e.getMessage());
        }
    }
}
