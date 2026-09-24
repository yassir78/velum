package org.chaosmaker;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.dispatcher.HandlerMapping;
import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;
import org.chaosmaker.handler.HealthCheckHandler;
import org.chaosmaker.handler.ProxyHandler;
import org.chaosmaker.health_check.HealthChecker;
import org.chaosmaker.http.client.HttpForwarder;
import org.chaosmaker.routing.RoundRobinRoutingEngine;
import org.chaosmaker.routing.RoutingStrategy;

public class Main {
    private static final int PORT = 9090;

    public static void main(String[] args) throws Exception {

        ServerPool pool = ServerPool.getInstance();

        HealthChecker healthChecker = new HealthChecker(pool);
        healthChecker.start(5);


        RoutingStrategy routingEngine = new RoundRobinRoutingEngine(pool);

        HttpForwarder forwarder = new HttpForwarder();

        HandlerMapping handlerMapping = new HandlerMapping();
        handlerMapping.registerRoute("/health", new HealthCheckHandler(pool));
        handlerMapping.setDefaultHandler(new ProxyHandler(routingEngine, forwarder));

        Dispatcher dispatcher = new Dispatcher(handlerMapping);

        FrontendListener listener = new FrontendListener(PORT, dispatcher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down load balancer...");
            listener.stop();
            healthChecker.stop();
        }));

        listener.start();
    }
}
