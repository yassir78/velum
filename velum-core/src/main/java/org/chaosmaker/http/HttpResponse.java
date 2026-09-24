package org.chaosmaker.http;

import java.util.Map;

import static org.chaosmaker.http.HttpStatus.BAD_GATEWAY;
import static org.chaosmaker.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.chaosmaker.http.HttpStatus.SERVICE_UNAVAILABLE;

public record HttpResponse(
        HttpStatus status,
        Map<String, String> headers,
        String body
) {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String HTML = "text/html";

    public static HttpResponse ok(String body) {
        return new HttpResponse(
                HttpStatus.OK,
                Map.of(CONTENT_TYPE, HTML),
                body
        );
    }

    public static HttpResponse serviceUnavailable(String message) {
        return new HttpResponse(
                SERVICE_UNAVAILABLE,
                Map.of(CONTENT_TYPE, HTML),
                message
        );
    }

    public static HttpResponse badGateway(String message) {
        return new HttpResponse(
                BAD_GATEWAY,
                Map.of(CONTENT_TYPE, HTML),
                message
        );
    }

    public static HttpResponse internalServerError(String message) {
        return new HttpResponse(
                INTERNAL_SERVER_ERROR,
                Map.of(CONTENT_TYPE, HTML),
                message
        );
    }
}