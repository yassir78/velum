# HTTP on Top of Sockets: Complete Guide with Java Implementation

## Understanding the Network Stack

### The Layered Model

HTTP doesn't exist in isolation—it's built on top of the TCP/IP protocol stack:

```
Application Layer    ← HTTP, HTTPS
Transport Layer      ← TCP, UDP
Internet Layer       ← IP (IPv4, IPv6)
Link Layer          ← Ethernet, WiFi
```

**HTTP = Structured messages over TCP sockets**

When you send an HTTP request, you're ultimately:
1. Creating a socket connection to a remote server (TCP handshake)
2. Writing text/binary data in HTTP format to that socket
3. Reading the response back from the socket
4. Closing the connection (or reusing it)

---

## TCP Sockets: The Foundation

### What is a Socket?

A socket is an **endpoint for network communication**. Think of it like a virtual mailbox:
- **Server socket**: Listens for incoming connections (like a mailbox at your address)
- **Client socket**: Connects to a server (like sending a letter to an address)

### TCP Connection Lifecycle

```
Client                                Server
  |
  |---- SYN -------->  (Client sends hello)
  |
  |<------- SYN-ACK --  (Server sends hello back)
  |
  |---- ACK -------->  (Client confirms)
  |
  |  [Connection established - Ready to send data]
  |
  |---- HTTP REQUEST -->
  |<-- HTTP RESPONSE --
  |
  |---- FIN -------->  (Close connection)
```

### Key Point
A socket is **just a bidirectional pipe for bytes**. TCP handles reliability (retries, ordering). HTTP is the **format** we put through that pipe.

---

## HTTP Protocol Fundamentals

### HTTP is Text-Based (Mostly)

An HTTP request looks like this:

```
GET /api/users HTTP/1.1
Host: example.com
Content-Type: application/json
Content-Length: 27

{"name": "John", "age": 30}
```

Breaking it down:
- **Line 1**: Method, Path, Protocol version
- **Lines 2-4**: Headers (key: value format)
- **Blank line**: Separates headers from body
- **Body**: Optional, depends on request type

An HTTP response:

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 23
Connection: close

{"status": "success"}
```

### Key HTTP Concepts

| Concept | Example |
|---------|---------|
| **Methods** | GET, POST, PUT, DELETE, PATCH |
| **Status Codes** | 200 (OK), 404 (Not Found), 500 (Server Error) |
| **Headers** | Metadata about request/response |
| **Body** | The actual data being sent |
| **Connection** | HTTP/1.1 keeps alive by default; HTTP/2 multiplexes |

---

## Java Socket Implementation

### Basic Echo Server (Receiving Data)

```java
import java.io.*;
import java.net.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080");
        
        while (true) {
            // Accept incoming connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());
            
            // Get input stream (read data from client)
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            // Read the HTTP request line by line
            String line;
            StringBuilder request = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                request.append(line).append("\n");
                System.out.println("Received: " + line);
            }
            
            // Send HTTP response
            OutputStream output = clientSocket.getOutputStream();
            String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: 13\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            "Hello, World!";
            
            output.write(response.getBytes());
            output.flush();
            
            // Close connection
            clientSocket.close();
        }
    }
}
```

**Important Details:**
- `ServerSocket`: Listens for incoming connections
- `Socket`: Represents connected client
- `\r\n`: HTTP requires CRLF (carriage return + line feed), not just `\n`
- Empty line signals end of headers

### HTTP Client (Sending Requests)

```java
import java.io.*;
import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        // Connect to server
        Socket socket = new Socket("localhost", 8080);
        
        // Get output stream (send data to server)
        OutputStream output = socket.getOutputStream();
        
        // Build HTTP request
        String request = "GET / HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";
        
        // Send request
        output.write(request.getBytes());
        output.flush();
        
        // Read response
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        
        // Close connection
        socket.close();
    }
}
```

---

## Building an HTTP Request Parser

### Parse HTTP Request Line

```java
public class HttpRequest {
    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> headers;
    private byte[] body;
    
    public static HttpRequest parse(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();
        request.headers = new HashMap<>();
        
        // Parse request line: "GET /api/users HTTP/1.1"
        String requestLine = reader.readLine();
        String[] parts = requestLine.split(" ");
        request.method = parts[0];      // GET
        request.path = parts[1];        // /api/users
        request.httpVersion = parts[2]; // HTTP/1.1
        
        // Parse headers
        String line;
        int contentLength = 0;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            String key = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();
            request.headers.put(key, value);
            
            if (key.equalsIgnoreCase("Content-Length")) {
                contentLength = Integer.parseInt(value);
            }
        }
        
        // Parse body if present
        if (contentLength > 0) {
            byte[] bodyBytes = new byte[contentLength];
            reader.read(bodyBytes);
            request.body = bodyBytes;
        }
        
        return request;
    }
    
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public byte[] getBody() { return body; }
}
```

---

## Building an HTTP Response Builder

```java
public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private byte[] body;
    
    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
    }
    
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
    
    public void setBody(byte[] body) {
        this.body = body;
        setHeader("Content-Length", String.valueOf(body.length));
    }
    
    public String toHttpString() {
        StringBuilder sb = new StringBuilder();
        
        // Status line
        sb.append(String.format("HTTP/1.1 %d %s\r\n", statusCode, statusMessage));
        
        // Headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        
        // Blank line
        sb.append("\r\n");
        
        return sb.toString();
    }
    
    public byte[] toBytes() {
        byte[] headerBytes = toHttpString().getBytes();
        if (body != null) {
            byte[] response = new byte[headerBytes.length + body.length];
            System.arraycopy(headerBytes, 0, response, 0, headerBytes.length);
            System.arraycopy(body, 0, response, headerBytes.length, body.length);
            return response;
        }
        return headerBytes;
    }
}
```

---

## HTTP Forwarder: Complete Example

This forwards HTTP requests from client → backend server:

```java
import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HttpForwarder {
    private String backendHost;
    private int backendPort;
    
    public HttpForwarder(String backendHost, int backendPort) {
        this.backendHost = backendHost;
        this.backendPort = backendPort;
    }
    
    public void handleClientRequest(Socket clientSocket) throws IOException {
        // Read client request
        BufferedReader clientReader = 
            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        HttpRequest clientRequest = HttpRequest.parse(clientReader);
        
        // Forward to backend
        Socket backendSocket = new Socket(backendHost, backendPort);
        OutputStream backendOutput = backendSocket.getOutputStream();
        
        // Reconstruct HTTP request
        String forwardRequest = String.format("%s %s %s\r\n",
            clientRequest.getMethod(),
            clientRequest.getPath(),
            "HTTP/1.1");
        
        // Forward headers (skip Host header, add new one)
        for (Map.Entry<String, String> header : clientRequest.getHeaders().entrySet()) {
            if (!header.getKey().equalsIgnoreCase("Host")) {
                forwardRequest += header.getKey() + ": " + header.getValue() + "\r\n";
            }
        }
        forwardRequest += "Host: " + backendHost + ":" + backendPort + "\r\n";
        forwardRequest += "\r\n";
        
        backendOutput.write(forwardRequest.getBytes());
        if (clientRequest.getBody() != null) {
            backendOutput.write(clientRequest.getBody());
        }
        backendOutput.flush();
        
        // Read backend response
        InputStream backendInput = backendSocket.getInputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        
        // Send response back to client
        OutputStream clientOutput = clientSocket.getOutputStream();
        while ((bytesRead = backendInput.read(buffer)) != -1) {
            clientOutput.write(buffer, 0, bytesRead);
        }
        clientOutput.flush();
        
        // Cleanup
        clientSocket.close();
        backendSocket.close();
    }
}
```

---

## Key Implementation Details

### 1. **CRLF Line Endings**
HTTP requires `\r\n` (carriage return + line feed):
```java
// ✓ Correct
String header = "GET / HTTP/1.1\r\n";

// ✗ Wrong (won't work in strict servers)
String header = "GET / HTTP/1.1\n";
```

### 2. **Content-Length is Critical**
The server must know when the body ends:
```java
byte[] body = getResponseBody();
headers.put("Content-Length", String.valueOf(body.length));
```

### 3. **Connection Management**
- Keep-alive (default in HTTP/1.1): Reuse connection for multiple requests
- Close: Send `Connection: close` to tell client/server to close

```java
// Signal close after response
headers.put("Connection", "close");
```

### 4. **Reading Request Body**
Read exactly `Content-Length` bytes, not "until stream ends":
```java
String contentLengthHeader = headers.get("Content-Length");
if (contentLengthHeader != null) {
    int length = Integer.parseInt(contentLengthHeader);
    byte[] body = new byte[length];
    input.read(body); // Read exactly 'length' bytes
}
```

### 5. **Thread Handling (Multiple Clients)**
```java
ServerSocket server = new ServerSocket(8080);
while (true) {
    Socket client = server.accept();
    new Thread(() -> {
        try {
            handleClient(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }).start();
}
```

---

## Common Pitfalls

| Problem | Solution |
|---------|----------|
| Server hangs waiting for headers | Use `\r\n`, not `\n` |
| Client doesn't receive full response | Set `Content-Length` header correctly |
| Keeps sending old data | Flush output stream: `output.flush()` |
| Can't connect to backend | Check firewall, hostname resolution, port |
| Headers keep getting added | Clear/recreate headers map, don't append |
| Hangs on reading body | Use `Content-Length`, don't read until EOF |

---

## Testing Your Implementation

### Test with curl
```bash
# Make a request to your server
curl -v http://localhost:8080/api/users

# POST with body
curl -X POST -d '{"name":"John"}' http://localhost:8080/api/users
```

### Test with netcat (debugging)
```bash
# Send raw HTTP
echo -e "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n" | nc localhost 8080
```

### Java HTTP Client (for testing)
```java
HttpURLConnection conn = (HttpURLConnection) 
    new URL("http://localhost:8080/test").openConnection();
conn.setRequestMethod("GET");
System.out.println(conn.getResponseCode()); // Should be 200
```

---

## Summary

- **Sockets** are bidirectional byte pipes (handled by TCP)
- **HTTP** is a text-based protocol that sends formatted messages through sockets
- **Requests**: Method + Path + Headers + Body
- **Responses**: Status code + Headers + Body
- **Key rules**: Use `\r\n`, set `Content-Length`, flush output, handle multi-byte data correctly

For your forwarder, the main loop is: **Receive HTTP request → Forward to backend → Send response back to client**.