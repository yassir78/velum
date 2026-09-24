package org.chaosmaker.server;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.http.reader.HttpRequestReader;
import org.chaosmaker.http.writer.HttpResponseWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection {
    private final Socket socket;
    private final HttpRequestReader requestReader;
    private final Dispatcher dispatcher;
    private final HttpResponseWriter responseWriter;


    public static ClientConnection of(Socket socket, Dispatcher dispatcher) {
        return new ClientConnection(socket, dispatcher);
    }

    public ClientConnection(Socket socket, Dispatcher dispatcher) {
        this.socket = socket;
        this.requestReader = new HttpRequestReader();
        this.dispatcher = dispatcher;
        this.responseWriter = new HttpResponseWriter();
    }

    public void handle() {
        try (
                socket;
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            HttpRequest request = requestReader.readRequest(in);

            HttpResponse response = dispatcher.dispatch(request);

            responseWriter.writeResponse(out, response);
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
