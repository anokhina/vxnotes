package io.vertx.ext.web.impl;

import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class RouterUtil {
    
    public static void remove(final Router router, final Route route) {
        if (router instanceof RouterImpl && route instanceof RouteImpl) {
            final RouteImpl routeImpl = (RouteImpl)route;
            final RouterImpl routerImpl = (RouterImpl)router;
            routerImpl.remove(routeImpl);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
