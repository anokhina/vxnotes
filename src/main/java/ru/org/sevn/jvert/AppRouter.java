/*
 * Copyright 2018 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.jvert;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class AppRouter {
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;
    
    private final Vertx vertx;
    private final long timeout;
    private final String appname;
    
    public AppRouter(final Vertx vertx) {
        this(vertx, LocalSessionStore.DEFAULT_SESSION_MAP_NAME, LocalSessionStore.DEFAULT_REAPER_INTERVAL);
    }
    
    public AppRouter(final Vertx vertx, final String appPrefix, final long timeout) {
        this.vertx = vertx;
        this.timeout =timeout;
        this.appname = appPrefix;
    }
    
    public Router router() {
        return setUpRouter(Router.router(vertx));
    }
    
    protected Router setUpRouter(Router router) {
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create().setBodyLimit(50 * MB));
        SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx, appname, timeout));
        router.route().handler(sessionHandler);
        return router;
    }
}
