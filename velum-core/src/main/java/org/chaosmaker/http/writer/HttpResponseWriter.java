package org.chaosmaker.http.writer;

import org.chaosmaker.http.model.HttpProtocol;
import org.chaosmaker.http.model.HttpRequest;
import org.chaosmaker.http.model.HttpResponse;
import org.chaosmaker.http.serializer.Http11RequestSerializer;
import org.chaosmaker.http.serializer.Http11ResponseSerializer;
import org.chaosmaker.http.serializer.HttpRequestSerializer;
import org.chaosmaker.http.serializer.HttpResponseSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponseWriter {

    private static final Map<HttpProtocol, HttpRequestSerializer> requestSerializers = Map.of(
            HttpProtocol.HTTP_1_1, new Http11RequestSerializer());

    private static final Map<HttpProtocol, HttpResponseSerializer> responseSerializers = Map.of(
            HttpProtocol.HTTP_1_1, new Http11ResponseSerializer());

    public void writeRequest(OutputStream output, HttpRequest request, String host) throws IOException {
        HttpRequestSerializer serializer = resolveRequestSerializer(request);
        output.write(serializer.serialize(request, host));
        output.flush();
    }

    public void writeResponse(OutputStream output, HttpResponse response) throws IOException {
        HttpResponseSerializer serializer = resolveResponseSerializer(response);
        output.write(serializer.serialize(response));
        output.flush();
    }

    private HttpRequestSerializer resolveRequestSerializer(HttpRequest request) {
        return requestSerializers.getOrDefault(request.protocol(), new Http11RequestSerializer());
    }

    private HttpResponseSerializer resolveResponseSerializer(HttpResponse response) {
        return responseSerializers.getOrDefault(response.protocol(), new Http11ResponseSerializer());
    }
}
