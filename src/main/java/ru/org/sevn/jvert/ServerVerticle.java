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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import ru.org.sevn.jvert.handler.LogoutHandler;
import ru.org.sevn.jvert.handler.ShowUserHandler;


public class ServerVerticle extends AbstractVerticle {
    
    private Router router;
    private final AppAuth appAuth = new AppAuth().setAuthorizer(new GroupUserAuthorizer(new JsonArray()));
    private final String appId;
    private final int port;
    
    public ServerVerticle(final String appId, final int port) {
        this.appId = appId;
        this.port = port;
    }

    public Router getRouter() {
        return router;
    }

    public AppAuth getAppAuth() {
        return appAuth;
    }

    @Override
    public void start() throws Exception {
        super.start();

        router = new AppRouter(vertx, appId, 1000*60*60).router();
        
        appAuth.bind(router);

        router.route("/user").handler(new ShowUserHandler());
        
        router.route("/logout").handler(new LogoutHandler());
        
        router.route("/*").handler(StaticHandler.create().setCachingEnabled(false).setDefaultContentEncoding("UTF-8"));
        
        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(config().getInteger("http.port", port));
    }
    
}
