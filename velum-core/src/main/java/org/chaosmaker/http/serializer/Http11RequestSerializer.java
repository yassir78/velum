package org.chaosmaker.http.serializer;

import org.chaosmaker.http.model.HttpRequest;

import java.util.Locale;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.chaosmaker.http.HttpUtils.*;
import static org.chaosmaker.http.serializer.HttpSerializerUtils.appendHeader;

public class Http11RequestSerializer implements HttpRequestSerializer {

    private static final String HOST = "Host";
    private static final String CONNECTION = "Connection";
    private static final String CONNECTION_CLOSE = "close";

    private static final Set<String> NOT_FORWARDED = Set.of(
            "host",
            "keep-alive",
            "proxy-connection",
            "transfer-encoding"
    );

    @Override
    public byte[] serialize(HttpRequest request, String host) {
        var message = new StringBuilder();

        appendRequestLine(message, request);

        appendHeader(message, HOST, host);

        appendHeader(message, CONNECTION, CONNECTION_CLOSE);

        request.headers().entrySet().stream()
                .filter(entry -> isForwardable(entry.getKey()))
                .forEach(entry -> appendHeader(message, entry.getKey(), entry.getValue()));

        message.append(CRLF);


        return message.toString().getBytes(UTF_8);
    }

    private static void appendRequestLine(StringBuilder message, HttpRequest request) {
        message.append(request.method()).append(SPACE)
                .append(request.path()).append(SPACE)
                .append(HTTP_VERSION).append(CRLF);
    }

    private static boolean isForwardable(String headerName) {
        return !NOT_FORWARDED.contains(headerName.toLowerCase(Locale.ROOT));
    }
}
