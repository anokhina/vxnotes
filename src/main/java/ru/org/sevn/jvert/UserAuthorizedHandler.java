/*
 * Copyright 2017 Veronica Anokhina.
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

import io.vertx.ext.web.RoutingContext;

public class UserAuthorizedHandler implements io.vertx.core.Handler<RoutingContext> {
    private final io.vertx.core.Handler<RoutingContext> handler;
    private final UserAuthorizer authorizer;
    private boolean runIfEnded;

    public UserAuthorizedHandler(UserAuthorizer authorizer, io.vertx.core.Handler<RoutingContext> handler) {
        this.handler = handler;
        this.authorizer = authorizer;
    }

    @Override
    public void handle(RoutingContext rc) {
        if (authorizer.isAllowed(rc.user(), rc)) {
            if (!rc.response().ended() || runIfEnded) {
                handler.handle(rc);
            }
        } else {
            rc.next();
        }
    }

    public boolean isRunIfEnded() {
        return runIfEnded;
    }

    public void setRunIfEnded(boolean runIfEnded) {
        this.runIfEnded = runIfEnded;
    }
}
