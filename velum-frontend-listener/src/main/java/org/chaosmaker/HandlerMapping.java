package org.chaosmaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMapping {
    private final Map<String, HttpHandler> routes = new ConcurrentHashMap<>();
    private HttpHandler defaultHandler; // The Proxy Handler

    public void registerRoute(String path, HttpHandler handler) {
        routes.put(path, handler);
    }

    public void setDefaultHandler(HttpHandler handler) {
        this.defaultHandler = handler;
    }

    public HttpHandler getHandler(String path) {
        // Return matching registered administrative handler, or fall back to standard proxying
        return routes.getOrDefault(path, defaultHandler);
    }
}
