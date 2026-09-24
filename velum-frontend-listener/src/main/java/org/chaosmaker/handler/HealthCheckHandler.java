package org.chaosmaker.handler;

import org.chaosmaker.domain.ServerPool;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;

public class HealthCheckHandler implements RequestHandler{
    public HealthCheckHandler(ServerPool pool) {
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.internalServerError("hihohoho \n");
    }
}
