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

import static java.lang.System.Logger.Level.INFO;

public class ClientConnection {
    private static final System.Logger LOG = System.getLogger(ClientConnection.class.getName());

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
            long start = System.nanoTime();

            HttpRequest request = requestReader.readRequest(in);

            HttpResponse response = dispatcher.dispatch(request);

            responseWriter.writeResponse(out, response);

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            LOG.log(INFO, "{0} {1} {2} -> {3,number,#} ({4,number,#} ms)",
                    socket.getRemoteSocketAddress(), request.method(), request.path(),
                    response.status().code(), elapsedMs);

        } catch (IOException e) {
            LOG.log(System.Logger.Level.WARNING, "Connection error from " + socket.getRemoteSocketAddress(), e);
        }
    }
}
