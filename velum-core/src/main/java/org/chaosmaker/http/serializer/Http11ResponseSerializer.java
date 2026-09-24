package org.chaosmaker.http.serializer;

import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.http.model.HttpStatus;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.chaosmaker.http.HttpUtils.*;
import static org.chaosmaker.http.serializer.HttpSerializerUtils.appendHeader;

/**
 * Serializes the status line and headers only. The body is not handled here.
 */
public class Http11ResponseSerializer implements HttpResponseSerializer {
    private static final System.Logger LOG = System.getLogger(Http11ResponseSerializer.class.getName());

    @Override
    public byte[] serialize(HttpResponse response) {
        var head = new StringBuilder();

        appendStatusLine(head, response.status());
        response.headers().forEach((name, value) -> appendHeader(head, name, value));
        head.append(CRLF);

        head.append(response.body());

        LOG.log(System.Logger.Level.DEBUG, () -> "Serialized response:\n" + head);
        return head.toString().getBytes(UTF_8);
    }

    private static void appendStatusLine(StringBuilder head, HttpStatus status) {
        head.append(HTTP_VERSION).append(SPACE)
                .append(status.code()).append(SPACE)
                .append(status.reason()).append(CRLF);
    }
}
