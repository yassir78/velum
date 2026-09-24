package org.chaosmaker.http;

public final class HttpUtils {

    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";
    public static final String SPACE = " ";
    public static final String HEADER_DELIMITER = ":";
    public static final String HEADER_SEPARATOR = HEADER_DELIMITER + SPACE;

    private HttpUtils() {
        // Utility class
    }
}
