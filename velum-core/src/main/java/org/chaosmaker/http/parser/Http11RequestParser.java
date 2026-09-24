package org.chaosmaker.http.parser;

import org.chaosmaker.http.model.HttpProtocol;
import org.chaosmaker.http.model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.chaosmaker.http.parser.HttpParserUtils.parseHeaders;
import static org.chaosmaker.http.parser.HttpParserUtils.splitStartLine;

public class Http11RequestParser implements HttpRequestParser {

    private static final int REQUEST_LINE_PARTS = 3;
    private static final String INVALID_REQUEST_LINE_MSG = "Invalid HTTP request line: ";

    @Override
    public HttpRequest parse(InputStream input) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input));

        String[] requestLine = parseRequestLine(reader.readLine());

        Map<String, String> headers = parseHeaders(reader.lines().takeWhile(line -> !line.isEmpty()));

        // TODO : handle body after blank line

        return new HttpRequest(
                requestLine[0],
                requestLine[1],
                HttpProtocol.fromValue(requestLine[2]),
                headers
        );
    }

    private String[] parseRequestLine(String requestLine) {
        String[] parts = splitStartLine(requestLine, INVALID_REQUEST_LINE_MSG);

        if (parts.length < REQUEST_LINE_PARTS) {
            throw new IllegalArgumentException(INVALID_REQUEST_LINE_MSG + requestLine);
        }

        return parts;
    }
}
