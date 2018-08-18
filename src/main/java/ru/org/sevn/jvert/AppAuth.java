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

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import ru.org.sevn.jsecure.PassAuth;

public class AppAuth {
        //https://vertx.io/docs/vertx-web/java/#_handling_sessions
    
    private String saltPrefix = "salt";
    private String invitePath = "sevn-http-vert-users.json";
    private FileAuthProvider fileAuthProvider;
    private UserAuthorizer authorizer;
    private String loginRoute = "/www/login";
    private String loginAuthRoute = "/www/loginauth";
    private io.vertx.ext.web.handler.AuthHandler authHandlerLogin;
    private String loginPage = "/www/loginpage.html";
    private String webroot = "www";
    private String path = "/www";
 
    public AppAuth() {
        
    }

    public String getSaltPrefix() {
        return saltPrefix;
    }

    public AppAuth setSaltPrefix(String saltPrefix) {
        this.saltPrefix = saltPrefix;
        return this;
    }

    public String getInvitePath() {
        return invitePath;
    }

    public AppAuth setInvitePath(String invitePath) {
        this.invitePath = invitePath;
        return this;
    }

    public AuthProvider getAuthProvider() {
        return fileAuthProvider;
    }

    public UserAuthorizer getAuthorizer() {
        return authorizer;
    }

    public AppAuth setAuthorizer(UserAuthorizer authorizer) {
        this.authorizer = authorizer;
        return this;
    }

    public String getLoginRoute() {
        return loginRoute;
    }

    public AppAuth setLoginRoute(String loginRoute) {
        this.loginRoute = loginRoute;
        return this;
    }

    public String getLoginAuthRoute() {
        return loginAuthRoute;
    }

    public AppAuth setLoginAuthRoute(String loginAuthRoute) {
        this.loginAuthRoute = loginAuthRoute;
        return this;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public AppAuth setLoginPage(String loginPage) {
        this.loginPage = loginPage;
        return this;
    }

    public AuthHandler getAuthHandlerLogin() {
        return authHandlerLogin;
    }

    public String getWebroot() {
        return webroot;
    }

    public AppAuth setWebroot(String webroot) {
        this.webroot = webroot;
        return this;
    }

    public String getPath() {
        return path;
    }

    public AppAuth setPath(String path) {
        this.path = path;
        return this;
    }
    
    
    public AppAuth bind(final Router router) throws IOException, NoSuchAlgorithmException {
        fileAuthProvider = new FileAuthProvider(new SimpleUserMatcher(invitePath), new PassAuth(saltPrefix));
        router.route().handler(UserSessionHandler.create(fileAuthProvider));
        
        authHandlerLogin = RedirectAuthHandler.create(getAuthProvider(), loginPage);
        router.route(getLoginRoute()).handler(authHandlerLogin);
        router.route(getLoginRoute()).handler(new WebpathHandler()); //TODO use the same
        router.route(getLoginAuthRoute()).handler(FormLoginHandler.create(getAuthProvider()));
        router.post(getLoginAuthRoute()).failureHandler(ctx -> {
            int statusCode = ctx.statusCode();
            if (statusCode == 400 || statusCode == 403) {
                //TODO wrong user name password message
                ctx.reroute(getLoginRoute());
            } else {
                ctx.next();
            }
        });
    
        router.route(path + "/*").handler(StaticHandler.create().setWebRoot(webroot).setCachingEnabled(false).setDefaultContentEncoding("UTF-8"));
        return this;
    }
}
