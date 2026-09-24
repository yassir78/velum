package org.chaosmaker.http.serializer;

import static org.chaosmaker.http.HttpUtils.CRLF;
import static org.chaosmaker.http.HttpUtils.HEADER_SEPARATOR;

final class HttpSerializerUtils {

    private HttpSerializerUtils() {
        // Utility class
    }

    static void appendHeader(StringBuilder message, String name, String value) {
        message.append(name)
                .append(HEADER_SEPARATOR)
                .append(value)
                .append(CRLF);
    }
}
