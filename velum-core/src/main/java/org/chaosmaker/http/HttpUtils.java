package org.chaosmaker.http;

import java.nio.charset.StandardCharsets;

public final class HttpUtils {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";

    private HttpUtils() {
        // Utility class
    }

    public static String statusLine(HttpResponse response) {
        HttpStatus status = response.status();
        return String.format(
                "%s %d %s%s",
                HTTP_VERSION,
                status.code(),
                status.reason(),
                CRLF
        );
    }

    public static String headers(HttpResponse response) {
        var headers = new StringBuilder();

        response.headers().forEach((name, value) ->
                headers.append(name)
                        .append(HEADER_SEPARATOR)
                        .append(value)
                        .append(CRLF)
        );

        headers.append(CONTENT_LENGTH)
                .append(HEADER_SEPARATOR)
                .append(contentLength(response.body()))
                .append(CRLF);

        return headers.toString();
    }

    public static int contentLength(String body) {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    public static String newLine() {
        return CRLF;
    }

    public static String buildRequest(String host, String path) {
        return """
            GET %s HTTP/1.1\r\n
            Host: %s\r\n
            Connection: close\r\n
            \r\n
            """.formatted(path, host);
    }
}
