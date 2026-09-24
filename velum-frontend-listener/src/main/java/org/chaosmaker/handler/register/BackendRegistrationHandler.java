package org.chaosmaker.handler.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;
import org.chaosmaker.handler.RequestHandler;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;

import java.util.List;

public class BackendRegistrationHandler implements RequestHandler {
    private static final String REGISTER_METHOD = "POST";

    private final ServerPool pool;
    private final ObjectMapper objectMapper;
    private final BackendRegistrationValidator validator;

    public BackendRegistrationHandler(ServerPool pool) {
        this.pool = pool;
        this.objectMapper = new ObjectMapper();
        this.validator = new BackendRegistrationValidator();
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (!REGISTER_METHOD.equalsIgnoreCase(request.method())) {
            return HttpResponse.methodNotAllowed("Only POST is supported on /backend\n");
        }

        BackendRegistrationRequest registration;

        try {
            registration = objectMapper.readValue(request.body(), BackendRegistrationRequest.class);
        } catch (JsonProcessingException e) {
            return HttpResponse.badRequest("Invalid JSON body: " + e.getOriginalMessage() + "\n");
        }

        List<String> errors = validator.validate(registration);

        if (!errors.isEmpty()) {
            return HttpResponse.badRequest("Invalid registration: " + String.join(", ", errors) + "\n");
        }

        Server server = new Server(registration.id(), registration.host(), registration.port());
        if (!pool.addServerIfAbsent(server)) {
            return HttpResponse.conflict("Backend already registered: " + registration.id() + "\n");
        }

        return HttpResponse.created("Registered backend " + server.getId() + " at " + server.getAddress() + "\n");
    }
}