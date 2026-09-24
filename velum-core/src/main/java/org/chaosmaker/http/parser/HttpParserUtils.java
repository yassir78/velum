package org.chaosmaker.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.chaosmaker.http.HttpUtils.HEADER_DELIMITER;
import static org.chaosmaker.http.HttpUtils.SPACE;

final class HttpParserUtils {

    private static final int MAX_START_LINE_PARTS = 3;
    private static final int MIN_START_LINE_PARTS = 2;
    private static final String INVALID_HEADER_MSG = "Invalid HTTP header: ";
    private static final String DUPLICATE_HEADER_JOINER = ", ";
    private static final String INVALID_CONTENT_LENGTH_MSG = "Invalid Content-Length: ";

    private HttpParserUtils() {
        // Utility class
    }

    static String[] splitStartLine(String startLine, String invalidMessage) {
        String[] parts = startLine.split(SPACE, MAX_START_LINE_PARTS);

        if (parts.length < MIN_START_LINE_PARTS) {
            throw new IllegalArgumentException(invalidMessage + startLine);
        }

        return parts;
    }

    static Map<String, String> parseHeaders(Stream<String> lines) {
        Map<String, String> headers = new LinkedHashMap<>();

        lines.map(HttpParserUtils::parseHeader)
                .forEach(header -> headers.merge(
                        header.getKey(),
                        header.getValue(),
                        (previous, next) -> previous + DUPLICATE_HEADER_JOINER + next
                ));

        return headers;
    }

    private static Map.Entry<String, String> parseHeader(String line) {
        String[] parts = line.split(HEADER_DELIMITER, 2);

        if (parts.length < 2) {
            throw new IllegalArgumentException(INVALID_HEADER_MSG + line);
        }

        return Map.entry(parts[0].trim(), parts[1].trim());
    }

    public static int extractContentLengthHeader(String value) {
        Objects.requireNonNull(value, INVALID_CONTENT_LENGTH_MSG);

        try {
            int length = parseInt(value.trim());
            if (length < 0) {
                throw new IllegalArgumentException(INVALID_CONTENT_LENGTH_MSG + value);
            }
            return length;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_CONTENT_LENGTH_MSG + value, e);
        }
    }


    public static String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            int read = reader.read(body, offset, contentLength - offset);

            if (read == -1) {
                throw new IOException("Unexpected end of HTTP response body: expected "
                        + contentLength + " chars, got " + offset);
            }

            offset += read;
        }

        return new String(body);
    }
}
