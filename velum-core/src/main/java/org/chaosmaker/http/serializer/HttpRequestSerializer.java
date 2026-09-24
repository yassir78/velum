package org.chaosmaker.http.serializer;

import org.chaosmaker.http.model.HttpRequest;

public interface HttpRequestSerializer {

    byte[] serialize(HttpRequest request, String host);
}
