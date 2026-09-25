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
    private static final System.Logger LOG = System.getLogger(BackendRegistrationHandler.class.getName());
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
            LOG.log(System.Logger.Level.DEBUG, "Rejected registration: invalid JSON: {0}", e.getOriginalMessage());
            return HttpResponse.badRequest("Invalid JSON body: " + e.getOriginalMessage() + "\n");
        }

        List<String> errors = validator.validate(registration);

        if (!errors.isEmpty()) {
            LOG.log(System.Logger.Level.DEBUG, "Rejected registration: {0}", errors);
            return HttpResponse.badRequest("Invalid registration: " + String.join(", ", errors) + "\n");
        }

        Server server = new Server(registration.id(), registration.host(), registration.port());
        if (!pool.addServerIfAbsent(server)) {
            LOG.log(System.Logger.Level.DEBUG, "Rejected registration: backend {0} already registered", server.getId());
            return HttpResponse.conflict("Backend already registered: " + registration.id() + "\n");
        }

        LOG.log(System.Logger.Level.INFO, "Registered backend {0} at {1}", server.getId(), server.getAddress());
        return HttpResponse.created("Registered backend " + server.getId() + " at " + server.getAddress() + "\n");
    }
}