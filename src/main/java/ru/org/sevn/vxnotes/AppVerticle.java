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
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.impl.RouterUtil;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import ru.org.sevn.common.FileUtil;
import ru.org.sevn.jvert.AppAuth;
import ru.org.sevn.jvert.ExtraUser;
import ru.org.sevn.jvert.UserAuthorizedHandler;
import ru.org.sevn.jvert.VertxOutputStream;
import ru.org.sevn.jvert.VertxUtil;
import ru.org.sevn.templ.TemplateEngine;

public class AppVerticle extends AbstractVerticle {
    
    private final ServerVerticle serverVerticle;
    private final List<Route> routers = new ArrayList<>();
    
    public AppVerticle(final ServerVerticle serverVerticle) {
        this.serverVerticle = serverVerticle;
    }
    
    static class DBVariantHandler implements Handler<RoutingContext> {
        private String webRoot;
        final TemplateEngine templateEngine = new TemplateEngine(null);
        private final String prefix;
        
        public DBVariantHandler(final String prefix) {
            this.prefix = prefix;
        }

        public String getWebRoot() {
            return webRoot;
        }

        public DBVariantHandler setWebRoot(String webRoot) {
            this.webRoot = webRoot;
            return this;
        }
        
        @Override
        public void handle(RoutingContext ctx) {
            //System.out.println("*********" + ctx.request().getParam("dbvariant"));
            final String dbvariant = ctx.request().getParam("dbvariant");
            final String fullPrefix = prefix + "db/" + dbvariant + "/";
            if (fullPrefix.length() < ctx.normalisedPath().length()) {
                final String path = ctx.normalisedPath().substring(fullPrefix.length());
                if (path.equals("events.html")) {
                    final HashMap<String, Object> params = new HashMap<>();
                    params.put("dbvariant", dbvariant);
                    outHtml(ctx, path, params);
                    return;
                } else {
                    VertxUtil.sendFile(ctx, new File(new File(webRoot), path));
                    return;
                }
            }
            ctx.next();
        }
        
        private void outHtml(final RoutingContext ctx, final String templateName, final Map<String, Object> params) {
            try {
                Template templ = templateEngine.getVelocityEngine().getTemplate(templateName);

                VelocityContext veloCtx = new VelocityContext(params);
                ctx.response().putHeader("content-type", "text/html").setChunked(true);
                try (VertxOutputStream vos = new VertxOutputStream(ctx.response())) {

                    OutputStreamWriter writer = new OutputStreamWriter(vos, "UTF-8");
                    templ.merge(veloCtx, writer);
                    writer.flush();
                }
            } catch (Exception ex) {
                Logger.getLogger(DBVariantHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    static class EventsAppHandler implements Handler<RoutingContext> {

        @Override
        public void handle(final RoutingContext ctx) {
            final String dbvariant = ctx.request().getParam("dbvariant");
            System.out.println("*********" + ctx.request().getParam("dbvariant"));
            JsonObject body = new JsonObject();
            try {
                body = ctx.getBodyAsJson();
            } catch (io.vertx.core.json.DecodeException ex) {}
            
            final JsonObject joresponse = new NoteService(DBManager.getSimpleSqliteObjectStore(
                    FileUtil.replaceForbidden(userName(ctx.user())), dbvariant
            )).execute(body.getString("sql"), body.getJsonArray("args"));
            if (joresponse.containsKey("errors")) {
                System.out.println("==" + body.encodePrettily());
                System.out.println(">>" + body.getString("sql"));
            }
            ctx.response().putHeader("content-type", "application/json")//; charset=utf-8
                    .end(joresponse.encode());
        }
        
    }
    
    @Override
    public void start() throws Exception {
        super.start();
        final Router router = serverVerticle.getRouter();
        final AppAuth appAuth = serverVerticle.getAppAuth();
        
        routers.add(router.route("/app/*").handler(appAuth.getAuthHandlerLogin()));
        routers.add(router.route("/db/*").handler(appAuth.getAuthHandlerLogin()));
        routers.add(router.route("/db/:dbvariant/app*").handler(new DBVariantHandler("/app/").setWebRoot("wwwapp")));
        routers.add(router.route("/app/*").handler(StaticHandler.create().setCachingEnabled(false).setWebRoot("wwwapp")));
        
        //routers.add(router.route("/vxnotes").handler(new RedirectUnAuthParamPageHandler("/test")));
        routers.add(router.route("/db/:dbvariant/vxnotes").handler(new UserAuthorizedHandler(appAuth.getAuthorizer(), new EventsAppHandler())));
        routers.add(router.route("/vxnotes").handler(new UserAuthorizedHandler(appAuth.getAuthorizer(), new EventsAppHandler())));
        
    }
    
    private static String userName(User u) {
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
