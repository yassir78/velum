package org.chaosmaker.handler;

import org.chaosmaker.domain.Server;
import org.chaosmaker.http.exception.ForwardingException;
import org.chaosmaker.http.client.HttpForwarder;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.routing.NoAvailableServerException;
import org.chaosmaker.routing.RoutingStrategy;

public class ProxyHandler implements RequestHandler {
    private static final System.Logger LOG = System.getLogger(ProxyHandler.class.getName());

    private final RoutingStrategy routingStrategy;
    private final HttpForwarder forwarder;

    public ProxyHandler(RoutingStrategy routingEngine, HttpForwarder forwarder) {
        this.routingStrategy = routingEngine;
        this.forwarder = forwarder;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            Server server = routingStrategy.pickServer()
                    .orElseThrow(() -> new NoAvailableServerException(
                            "No healthy servers available for routing"));

            LOG.log(System.Logger.Level.DEBUG, "Routing {0} {1} to {2} ({3})",
                    request.method(), request.path(), server.getId(), server.getAddress());
            return forwarder.forward(request, server);
        } catch (NoAvailableServerException e) {
            LOG.log(System.Logger.Level.WARNING, "No healthy backend for {0} {1}", request.method(), request.path());
            return HttpResponse.serviceUnavailable(e.getMessage());
        } catch (ForwardingException e) {
            LOG.log(System.Logger.Level.WARNING, "Failed to forward " + request.method() + " " + request.path(), e);
            return HttpResponse.badGateway("Failed to forward request: " + e.getMessage());
        } catch (Exception e) {
            LOG.log(System.Logger.Level.ERROR, "Unexpected proxy error for " + request.method() + " " + request.path(), e);
            return HttpResponse.internalServerError("Proxy error: " + e.getMessage());
        }
    }
}