package org.chaosmaker.http.model;

import java.util.Map;

import static org.chaosmaker.http.model.HttpStatus.BAD_GATEWAY;
import static org.chaosmaker.http.model.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.chaosmaker.http.model.HttpStatus.SERVICE_UNAVAILABLE;

public record HttpResponse(
        HttpProtocol protocol,
        HttpStatus status,
        Map<String, String> headers,
        String body
) {

    private static final HttpProtocol DEFAULT_PROTOCOL = HttpProtocol.HTTP_1_1;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String HTML = "text/html";

    public static HttpResponse ok(String body) {
        return new HttpResponse(
                DEFAULT_PROTOCOL,
                HttpStatus.OK,
                Map.of(CONTENT_TYPE, HTML),
                body
        );
    }

    public static HttpResponse serviceUnavailable(String message) {
        return new HttpResponse(
                DEFAULT_PROTOCOL,
                SERVICE_UNAVAILABLE,
                Map.of(CONTENT_TYPE, HTML),
                message
        );
    }

    public static HttpResponse badGateway(String message) {
        return new HttpResponse(
                DEFAULT_PROTOCOL,
                BAD_GATEWAY,
                Map.of(CONTENT_TYPE, HTML),
                message
        );
    }

    public static HttpResponse internalServerError(String message) {
        return new HttpResponse(
                DEFAULT_PROTOCOL,
                INTERNAL_SERVER_ERROR,
                Map.of(CONTENT_TYPE, HTML),
                message
        );
    }
}