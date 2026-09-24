package org.chaosmaker.http.model;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        HttpProtocol protocol,
        Map<String, String> headers
) {
}
