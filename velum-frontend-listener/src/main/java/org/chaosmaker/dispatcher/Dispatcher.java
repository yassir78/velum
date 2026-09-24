package org.chaosmaker.dispatcher;


import org.chaosmaker.handler.RequestHandler;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;

public class Dispatcher {
    private final HandlerMapping handlerMapping;

    public Dispatcher(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public HttpResponse dispatch(HttpRequest request) {

        RequestHandler handler =
                handlerMapping.getHandler(request.path());

        return handler.handle(request);
    }
}
