package org.chaosmaker.handler;

import org.chaosmaker.http.HttpRequest;
import org.chaosmaker.http.HttpResponse;

public class LoadBalancerHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        // 1. Read load balancer configuration
        // 2. Select backend
        // 3. Forward request
        // 4. Return backend response
        return HttpResponse.ok("load balanced");
    }
}
