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
package ru.org.sevn.vxnotes;

import ru.org.sevn.jvert.ServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterUtil;
import java.util.ArrayList;
import java.util.List;
import ru.org.sevn.jvert.AppAuth;
import ru.org.sevn.jvert.ExtraUser;
import ru.org.sevn.jvert.UserAuthorizedHandler;

public class AppVerticle extends AbstractVerticle {
    
    private final ServerVerticle serverVerticle;
    private final List<Route> routers = new ArrayList<>();
    
    public AppVerticle(final ServerVerticle serverVerticle) {
        this.serverVerticle = serverVerticle;
    }
    
    @Override
    public void start() throws Exception {
        super.start();
        final Router router = serverVerticle.getRouter();
        final AppAuth appAuth = serverVerticle.getAppAuth();
        
        routers.add(router.route("/app/*").handler(appAuth.getAuthHandlerLogin()));
        routers.add(router.route("/app/*").handler(StaticHandler.create().setCachingEnabled(false).setWebRoot("wwwapp")));
        
        //routers.add(router.route("/vxnotes").handler(new RedirectUnAuthParamPageHandler("/test")));
        routers.add(router.route("/vxnotes").handler(new UserAuthorizedHandler(appAuth.getAuthorizer(), ctx -> {
            JsonObject body = new JsonObject();
            try {
                body = ctx.getBodyAsJson();
            } catch (io.vertx.core.json.DecodeException ex) {}
            
            final JsonObject joresponse = new NoteService(DBManager.getSimpleSqliteObjectStore(replaceForbidden(userName(ctx.user())))).execute(body.getString("sql"), body.getJsonArray("args"));
            if (joresponse.containsKey("errors")) {
                System.out.println("==" + body.encodePrettily());
                System.out.println(">>" + body.getString("sql"));
            }
            ctx.response().putHeader("content-type", "application/json")//; charset=utf-8
                    .end(joresponse.encode());
        })));
        
    }
    
    private String replaceForbidden(String str) {
        return str.replaceAll("[^\\dA-Za-z ]", "_").replaceAll("\\s+", "-");
    }
    
    private String userName(User u) {
        if (u != null) {
            if (u instanceof ExtraUser) {
                ExtraUser user = (ExtraUser) u;
                return user.getId();
            }
            JsonObject principal = u.principal();
            try {
                for(final String k : principal.fieldNames()) {
                    if (k.toLowerCase().contains("user") || k.toLowerCase().contains("login")) {
                        return principal.getString(k);
                    }
                }
            } catch (Exception e) {}
        }
        
        return "demo";
    }
    
    @Override
    public void stop() throws Exception {
        try {
            for (final Route route : routers) {
                    RouterUtil.remove(serverVerticle.getRouter(), route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}
