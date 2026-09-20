package org.chaosmaker;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public record HttpRequestContext(
        String method,
        String path,
        Map<String, String> headers,
        InputStream requestBody,
        OutputStream clientResponseStream
) {
}
