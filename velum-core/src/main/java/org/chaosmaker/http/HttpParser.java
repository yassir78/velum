package org.chaosmaker.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {

    public HttpRequest parse(InputStream input) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input));

        var requestLine = reader.readLine();
        var requestParts = parseRequestLine(requestLine);

        Map<String, String> headers = parseHeaders(reader);

        return new HttpRequest(
                requestParts[0],
                requestParts[1],
                headers
        );
    }

    private String[] parseRequestLine(String requestLine) {
        String[] parts = requestLine.split(" ", 3);

        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }

        return parts;
    }

    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);

            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid HTTP header: " + line);
            }

            String name = parts[0].trim();
            String value = parts[1].trim();

            headers.put(name, value);
        }

        return headers;
    }
}