package org.chaosmaker.http.connector;

import org.chaosmaker.domain.Server;

import java.io.IOException;
import java.net.Socket;

public interface BackendConnector {

    Socket connect(Server server) throws IOException;
}
