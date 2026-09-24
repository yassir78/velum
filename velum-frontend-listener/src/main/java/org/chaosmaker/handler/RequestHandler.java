package org.chaosmaker.handler;


import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request);
}
