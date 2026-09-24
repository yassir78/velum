package org.chaosmaker.http.serializer;

import org.chaosmaker.http.model.HttpResponse;

public interface HttpResponseSerializer {

    byte[] serialize(HttpResponse response);
}
