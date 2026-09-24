package org.chaosmaker.handler;


import org.chaosmaker.http.HttpRequest;
import org.chaosmaker.http.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request);
}
