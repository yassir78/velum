package org.chaosmaker.http.reader;

import org.chaosmaker.http.model.HttpProtocol;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.http.parser.Http11RequestParser;
import org.chaosmaker.http.parser.Http11ResponseParser;
import org.chaosmaker.http.parser.HttpRequestParser;
import org.chaosmaker.http.parser.HttpResponseParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpRequestReader {

    private static final Map<HttpProtocol, HttpRequestParser> requestParsers = Map.of(
            HttpProtocol.HTTP_1_1, new Http11RequestParser()
    );

    private static final Map<HttpProtocol, HttpResponseParser> responseParsers = Map.of(
            HttpProtocol.HTTP_1_1, new Http11ResponseParser()
    );

    public HttpRequest readRequest(InputStream input) throws IOException {
        HttpRequestParser parser = resolveRequestParser();
        return parser.parse(input);
    }

    public HttpResponse readResponse(InputStream input) throws IOException {
        HttpResponseParser parser = resolveResponseParser();
        return parser.parse(input);
    }

    private HttpRequestParser resolveRequestParser() {
        // TODO : handle resolving logic by getting the protocol from the first line
        return requestParsers.get(HttpProtocol.HTTP_1_1);
    }

    private HttpResponseParser resolveResponseParser() {
        // TODO : handle resolving logic by getting the protocol from the first line
        return responseParsers.get(HttpProtocol.HTTP_1_1);
    }
}
