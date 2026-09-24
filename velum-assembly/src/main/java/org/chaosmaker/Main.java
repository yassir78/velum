package org.chaosmaker;

import org.chaosmaker.dispatcher.Dispatcher;
import org.chaosmaker.dispatcher.HandlerMapping;
import org.chaosmaker.domain.Server;
import org.chaosmaker.domain.ServerPool;
import org.chaosmaker.handler.backends.BackendListHandler;
import org.chaosmaker.handler.register.BackendRegistrationHandler;
import org.chaosmaker.handler.register.BackendRegistrationHandler;
import org.chaosmaker.handler.HealthCheckHandler;
import org.chaosmaker.handler.ProxyHandler;
import org.chaosmaker.health_check.HealthChecker;
import org.chaosmaker.http.client.HttpForwarder;
import org.chaosmaker.routing.RoundRobinRoutingEngine;
import org.chaosmaker.routing.RoutingStrategy;

public class Main {
    private static final System.Logger LOG = System.getLogger(Main.class.getName());
    private static final int PORT = 9090;
    private static final int HEALTH_CHECK_INTERVAL_SECONDS = 5;

    public static void main(String[] args) throws Exception {

        ServerPool pool = ServerPool.getInstance();

        HealthChecker healthChecker = new HealthChecker(pool);
        healthChecker.start(HEALTH_CHECK_INTERVAL_SECONDS);


        RoutingStrategy routingEngine = new RoundRobinRoutingEngine(pool);

        HttpForwarder forwarder = new HttpForwarder();

        HandlerMapping handlerMapping = new HandlerMapping();
        handlerMapping.registerRoute("/health", new HealthCheckHandler(pool));
        handlerMapping.registerRoute("/backend", new BackendRegistrationHandler(pool));
        handlerMapping.registerRoute("/backends", new BackendListHandler(pool));
        handlerMapping.setDefaultHandler(new ProxyHandler(routingEngine, forwarder));

        Dispatcher dispatcher = new Dispatcher(handlerMapping);

        FrontendListener listener = new FrontendListener(PORT, dispatcher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.log(System.Logger.Level.INFO, "Shutting down load balancer");
            listener.stop();
            healthChecker.stop();
        }));

        LOG.log(System.Logger.Level.INFO, "Starting load balancer on port {0,number,#}, health check every {1,number,#}s",
                PORT, HEALTH_CHECK_INTERVAL_SECONDS);
        listener.start();
    }
}
