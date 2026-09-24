package org.chaosmaker.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class HttpResponseWriter {

    public void write(
            OutputStream output,
            HttpResponse response
    ) throws IOException {

        PrintWriter writer = new PrintWriter(output);

        writer.print(HttpUtils.statusLine(response));
        writer.print(HttpUtils.headers(response));
        writer.print(HttpUtils.newLine());
        writer.print(response.body());

        writer.flush();
    }
}
