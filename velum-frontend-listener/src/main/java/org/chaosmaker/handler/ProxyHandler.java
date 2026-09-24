package org.chaosmaker.handler;

import org.chaosmaker.domain.Server;
import org.chaosmaker.http.exception.ForwardingException;
import org.chaosmaker.http.client.HttpForwarder;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.routing.NoAvailableServerException;
import org.chaosmaker.routing.RoutingStrategy;

public class ProxyHandler implements RequestHandler {
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

            return forwarder.forward(request, server);
        } catch (NoAvailableServerException e) {
            return HttpResponse.serviceUnavailable(e.getMessage());
        } catch (ForwardingException e) {
            return HttpResponse.badGateway("Failed to forward request: " + e.getMessage());
        } catch (Exception e) {
            return HttpResponse.internalServerError("Proxy error: " + e.getMessage());
        }
    }
}