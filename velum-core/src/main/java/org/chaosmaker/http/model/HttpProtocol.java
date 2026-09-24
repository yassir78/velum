package org.chaosmaker.http.model;

import java.util.Arrays;

public enum HttpProtocol {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpProtocol(String value) {
        this.value = value;
    }

    public static HttpProtocol fromValue(String value) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported HTTP protocol: " + value));
    }

    public String value() {
        return value;
    }
}
