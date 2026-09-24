package org.chaosmaker.http.parser;

import org.chaosmaker.http.model.HttpProtocol;
import org.chaosmaker.http.model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.chaosmaker.http.HttpUtils.EMPTY;
import static org.chaosmaker.http.parser.HttpParserUtils.extractContentLengthHeader;
import static org.chaosmaker.http.parser.HttpParserUtils.parseHeaders;
import static org.chaosmaker.http.parser.HttpParserUtils.readBody;
import static org.chaosmaker.http.parser.HttpParserUtils.splitStartLine;

public class Http11RequestParser implements HttpRequestParser {

    private static final int REQUEST_LINE_PARTS = 3;
    private static final String INVALID_REQUEST_LINE_MSG = "Invalid HTTP request line: ";
    private static final String CONTENT_LENGTH = "Content-Length";

    @Override
    public HttpRequest parse(InputStream input) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input));

        String[] requestLine = parseRequestLine(reader.readLine());

        Map<String, String> headers = parseHeaders(reader.lines().takeWhile(line -> !line.isEmpty()));

        String contentLength = headers.get(CONTENT_LENGTH);

        String body = nonNull(contentLength) ? readBody(reader, extractContentLengthHeader(contentLength)) : EMPTY;

        return new HttpRequest(
                requestLine[0],
                requestLine[1],
                HttpProtocol.fromValue(requestLine[2]),
                headers,
                body
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
