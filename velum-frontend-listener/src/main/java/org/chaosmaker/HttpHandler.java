package org.chaosmaker;

@FunctionalInterface
public interface HttpHandler {
    void handle(HttpRequestContext context) throws Exception;
}
