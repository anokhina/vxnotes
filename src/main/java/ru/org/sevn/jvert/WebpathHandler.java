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

import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

public class WebpathHandler implements io.vertx.core.Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        String rpath = "/";
        User user = ctx.user();
        if (user instanceof ExtraUser) {
            ExtraUser euser = (ExtraUser)user;
            if (euser.getLocalExtraData() != null) {
                rpath = euser.getLocalExtraData().getString("webpath", "/");
            }
        }
        ctx.reroute(rpath); 
    }
    
}
