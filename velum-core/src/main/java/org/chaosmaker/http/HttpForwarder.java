package org.chaosmaker.http;

import org.chaosmaker.domain.Server;
import org.chaosmaker.http.exception.ForwardingException;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public class HttpForwarder {
    private static final HttpClient client = HttpClient.newHttpClient();

    public HttpResponse forward(HttpRequest request, Server server) {

        try {
            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(server.getUri() + request.path()))
                    .header("Accept", "application/json")
                    .header("User-Agent", "shopnow-backend/1.0")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            java.net.http.HttpResponse<String> send = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            return HttpResponse.ok(send.body());
        } catch (Exception e) {
            throw new ForwardingException(e.getMessage());
        }

    }
}
