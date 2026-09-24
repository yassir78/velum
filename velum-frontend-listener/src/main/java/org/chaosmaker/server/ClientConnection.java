package org.chaosmaker.server;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.http.HttpParser;
import org.chaosmaker.http.HttpRequest;
import org.chaosmaker.http.HttpResponse;
import org.chaosmaker.http.HttpResponseWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection {
    private final Socket socket;
    private final HttpParser parser;
    private final Dispatcher dispatcher;
    private final HttpResponseWriter responseWriter;

    public ClientConnection(Socket socket, Dispatcher dispatcher) {
        this.socket = socket;
        this.parser = new HttpParser();
        this.dispatcher = dispatcher;
        this.responseWriter = new HttpResponseWriter();
    }

    public void handle() {
        try (
                socket;
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            HttpRequest request = parser.parse(in);

            HttpResponse response = dispatcher.dispatch(request);

            responseWriter.write(out, response);

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
