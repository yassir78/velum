package org.chaosmaker.http.parser;

import org.chaosmaker.http.model.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

public interface HttpRequestParser {

    HttpRequest parse(InputStream input) throws IOException;
}
