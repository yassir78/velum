package org.chaosmaker.dispatcher;

import org.chaosmaker.handler.RequestHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMapping {
    private final Map<String, RequestHandler> handlers = new ConcurrentHashMap<>();

    private RequestHandler defaultHandler;

    public void registerRoute(String path, RequestHandler handler) {
        handlers.put(path, handler);
    }

    public RequestHandler getHandler(String path) {
        return handlers.getOrDefault(path, defaultHandler);
    }

    public void setDefaultHandler(RequestHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }
}
