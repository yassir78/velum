package org.chaosmaker;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.dispatcher.HandlerMapping;
import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;
import org.chaosmaker.handler.HealthCheckHandler;
import org.chaosmaker.handler.ProxyHandler;
import org.chaosmaker.http.client.HttpForwarder;
import org.chaosmaker.routing.RoundRobinRoutingEngine;
import org.chaosmaker.routing.RoutingStrategy;

/*
 - fix forwarder to use socket too
 - clean the code really well using SOLID and design patterns
 - implement health check
 - add configuration
 */
public class Main {
    private final static int PORT = 9090;

    public static void main(String[] args) throws Exception {

        // 1. Initialize Core State Pool
        ServerPool pool = new ServerPool();
        pool.addServer(new Server("backend-1", "localhost", 8080));
        pool.addServer(new Server("backend-2", "localhost", 8081));

        /*
        HealthChecker healthChecker = new HealthChecker(pool);
        healthChecker.start(5);
         */

        RoutingStrategy routingEngine = new RoundRobinRoutingEngine(pool);

        HttpForwarder forwarder = new HttpForwarder();

        // 4. Setup Dispatcher & Routes (Spring-style Handler Mapping)
        HandlerMapping handlerMapping = new HandlerMapping();
        handlerMapping.registerRoute("/health", new HealthCheckHandler(pool)); // LB status
        handlerMapping.setDefaultHandler(new ProxyHandler(routingEngine, forwarder)); // Proxy requests*/

        Dispatcher dispatcher = new Dispatcher(handlerMapping);

        FrontendListener listener = new FrontendListener(PORT, dispatcher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down load balancer...");
            listener.stop();
            // healthChecker.stop();
        }));

        listener.start();
    }
}
