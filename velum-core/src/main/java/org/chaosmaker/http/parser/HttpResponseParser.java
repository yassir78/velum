package org.chaosmaker.http.parser;

import org.chaosmaker.http.model.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

public interface HttpResponseParser {

    HttpResponse parse(InputStream input) throws IOException;
}
