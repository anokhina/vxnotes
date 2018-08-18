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

import ru.org.sevn.jvert.IdResultHandler;
import ru.org.sevn.jvert.ServerVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.org.sevn.jvert.StringIdHolder;

public class Main {
    
    
    public static void main(String[] args) {
        final File settingsFile = new File(new File(System.getProperty("user.home")), ".vxnotes.json");
        String salt = "salt";
        int port = 7777;
        try {
            JsonObject settings = new JsonObject(new String(Files.readAllBytes(settingsFile.toPath()), "UTF-8"));
            if (settings.containsKey("salt")) {
                salt = settings.getString("salt");
            }
            if (settings.containsKey("port")) {
                port = settings.getInteger("port");
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        final Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(2L * 1000 * 1000000));
        final ServerVerticle serverVerticle = new ServerVerticle("ru.org.sevn.vxnotes", port);
        serverVerticle.getAppAuth().setSaltPrefix(salt).setInvitePath(new File(new File(System.getProperty("user.home")), "sevn-http-vert-users.json").getAbsolutePath());
        
        final AppVerticle appVerticle = new AppVerticle(serverVerticle);
        final StringIdHolder appVerticleId = new StringIdHolder();
        
        vertx.deployVerticle(serverVerticle, res -> { 
            new IdResultHandler(new StringIdHolder()).handle(res); 
            vertx.deployVerticle(appVerticle, new IdResultHandler(appVerticleId));
        });
        
//        try {
//            Thread.sleep(120000);
//            vertx.undeploy(appVerticleId.getId(), res -> {
//                System.out.println("The verticle has been undeployed. ");
//            });
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
