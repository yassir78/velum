package org.chaosmaker.handler.backends;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chaosmaker.domain.ServerPool;
import org.chaosmaker.handler.RequestHandler;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;

import java.util.List;

public class BackendListHandler implements RequestHandler {
    private static final String LIST_METHOD = "GET";

    private final ServerPool pool;
    private final ObjectMapper objectMapper;

    public BackendListHandler(ServerPool pool) {
        this.pool = pool;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (!LIST_METHOD.equalsIgnoreCase(request.method())) {
            return HttpResponse.methodNotAllowed("Only GET is supported on /backends\n");
        }

        List<BackendResponse> backends = pool
                .getAllServers().stream()
                .map(BackendResponse::from)
                .toList();

        try {
            return HttpResponse.okJson(objectMapper.writeValueAsString(backends));
        } catch (JsonProcessingException e) {
            return HttpResponse.internalServerError("Failed to serialize backends: " + e.getOriginalMessage() + "\n");
        }
    }
}
