package org.chaosmaker.http.parser;

import org.chaosmaker.http.model.HttpProtocol;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.http.model.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.chaosmaker.http.parser.HttpParserUtils.*;

public class Http11ResponseParser implements HttpResponseParser {

    private static final String INVALID_STATUS_LINE_MSG = "Invalid HTTP status line: ";
    private static final String CONTENT_LENGTH = "Content-Length";

    @Override
    public HttpResponse parse(InputStream input) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input));

        String[] responseLine = parseResponseLine(reader.readLine());

        HttpProtocol protocol = HttpProtocol.fromValue(responseLine[0]);

        HttpStatus status = HttpStatus.fromValue(responseLine[1]);

        var headers = parseHeaders(
                reader
                        .lines()
                        .takeWhile(line -> !line.isEmpty()));

        int contentLength = extractContentLengthHeader(headers.get(CONTENT_LENGTH));

        String body = readBody(reader, contentLength);

        return new HttpResponse(
                protocol,
                status,
                headers,
                body);
    }

    private String[] parseResponseLine(String responseLine) {
        return splitStartLine(responseLine, INVALID_STATUS_LINE_MSG);
    }


}
