# Velum

Velum is a small HTTP/1.1 load balancer written in plain Java 21, with no frameworks. It speaks HTTP over raw
`java.net.Socket`s. It parses requests, picks a healthy backend with round-robin, forwards the request, and
writes the backend's response back to the client. Each connection runs on its own virtual thread.

It is a learning project: the goal is to understand what a load balancer does at the socket level, so the
HTTP parser, serializer and forwarder are written by hand.

## Features

- **Hand-written HTTP/1.1**: request/response parsing and serialization, including `Content-Length` bodies
- **Round-robin routing** across healthy backends
- **Health checking**: a scheduled TCP probe marks unreachable backends as down
- **Runtime backend registration** through a small admin API (`POST /backend`, `GET /backends`)
- **Virtual threads**: one per client connection (`Executors.newVirtualThreadPerTaskExecutor()`)
- **Clear error mapping**: `503` when no backend is healthy, `502` when forwarding fails

## Architecture

```
            ┌───────────────────────── velum (:9090) ─────────────────────────┐
 client ──► │ FrontendListener ─► ClientConnection ─► Dispatcher ─► Handler   │
            │  (virtual thread     (read request,      (path →      │         │
            │   per connection)     write response)    handler)     │         │
            │                                                       ▼         │
            │   /backend  → BackendRegistrationHandler      ProxyHandler      │
            │   /backends → BackendListHandler                  │             │
            │   /health   → HealthCheckHandler                  ▼             │
            │                                  RoundRobinRoutingEngine        │
            │                                                   │             │
            │   HealthChecker ──(TCP probe)──► ServerPool ◄─────┘             │
            │                                                   │             │
            │                                   HttpForwarder ──┴──► backend  │
            └─────────────────────────────────────────────────────────────────┘
```

### Modules

| Module                    | Responsibility                                                                                     |
|---------------------------|----------------------------------------------------------------------------------------------------|
| `velum-core`              | Domain (`Server`, `ServerPool`), HTTP model/parser/serializer, backend connector, routing, health checks |
| `velum-frontend-listener` | TCP listener, per-connection handling, dispatcher and request handlers                            |
| `velum-assembly`          | `Main`: wires the components together and starts the listener on port `9090`                     |

### Key packages (`velum-core`)

| Package          | Contents                                                                  |
|------------------|---------------------------------------------------------------------------|
| `http.model`     | `HttpRequest`, `HttpResponse`, `HttpStatus`, `HttpProtocol`               |
| `http.parser`    | `Http11RequestParser`, `Http11ResponseParser`                             |
| `http.serializer`| `Http11RequestSerializer`, `Http11ResponseSerializer`                     |
| `http.connector` | `SocketBackendConnector`: opens sockets to backends with timeouts         |
| `http.client`    | `HttpForwarder`: sends a request to a backend and reads the response      |
| `routing`        | `RoutingStrategy`, `RoundRobinRoutingEngine`                              |
| `health_check`   | `HealthChecker`: periodic TCP probe of every registered backend           |

## Getting started

### Prerequisites

- JDK 21+
- Maven 3.8+

### Build

```bash
mvn clean install
```

### Run

```bash
mvn -q -pl velum-assembly dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "velum-assembly/target/classes:$(cat velum-assembly/cp.txt)" org.chaosmaker.Main
```

Or run `org.chaosmaker.Main` from your IDE. Velum listens on **port 9090**.

### Start some backends

Any HTTP server works as a backend, for example:

```bash
python3 -m http.server 8081
```

`test/start-backends.sh` starts four instances of the companion
`velum-client` test server on ports 8081–8084. It expects that project to be
checked out next to this one (`../velum-client`):

```bash
./test/start-backends.sh        # ports 8081-8084
./test/start-backends.sh 9001   # ports 9001-9004
```

## Admin API

### Register a backend: `POST /backend`

```bash
curl -i -X POST localhost:9090/backend \
  -H 'Content-Type: application/json' \
  -d '{"id": "backend-1", "host": "localhost", "port": 8081}'
```

| Status            | When                                                        |
|-------------------|-------------------------------------------------------------|
| `201 Created`     | Backend added to the pool                                   |
| `400 Bad Request` | Invalid JSON, missing `id`/`host`/`port`, or port out of range |
| `405`             | Method other than `POST`                                    |
| `409 Conflict`    | A backend with the same `id` is already registered          |

### List backends: `GET /backends`

```bash
curl localhost:9090/backends
```

```json
[{"id":"backend-1","host":"localhost","port":8081,"alive":true,"activeConnections":0}]
```

### Proxying

Any other path goes to the next healthy backend:

```bash
curl -i localhost:9090/
```

## Limitations

Velum is a work in progress. Current limitations:

- One request per connection (no keep-alive or pipelining)
- No chunked transfer encoding; bodies need a `Content-Length`
- A backend that is marked down is never marked healthy again, and the health check is a plain TCP connect
- Port and health-check interval are hard-coded in `Main`
- `/health` is a placeholder

## Roadmap

These are the target requirements for the project:

- Layer 4 (TCP/UDP) and layer 7 (HTTP/HTTPS) load balancing
- High throughput (target: 1M requests/second)
- Health checking that routes around unhealthy servers, and brings them back when they recover
- Configurable sticky sessions for stateful applications
- SSL termination
- External configuration (ports, strategies, backends)
- More routing strategies (least connections, weighted)

## Further reading

`velum-assembly/src/main/resources/http.md` is a guide to how HTTP works on top of TCP sockets in Java, written
as background for this project.
