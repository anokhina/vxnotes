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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VertxUtil {
    
    public static String getContentType(final String fileName) {
        return io.vertx.core.http.impl.MimeMapping.getMimeTypeForFilename(fileName);
    }
    
    public static String getContentType(final File file) {
        try {
            return Files.probeContentType(Paths.get(file.getPath()));
        } catch (IOException ex) {
            Logger.getLogger(VertxUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getContentType(file.getName());
    }

    public static void sendFile(final RoutingContext ctx, final File f) {
        if (f.exists()) {
            final String contentType = getContentType(f);
            if (contentType != null) {
                ctx.response().putHeader("content-type", contentType);
            }
            ctx.response().sendFile(f.getPath(), res2 -> {
                if (res2.failed()) {
                    ctx.fail(res2.cause());
                }
            });
        }

    }
    
    public static void sendBytes(final RoutingContext ctx, final String contentType, final byte[] bytes) {
        ctx.response().putHeader("content-type", contentType);

        try (VertxOutputStream vos = new VertxOutputStream(ctx.response())){
            ctx.response().setChunked(true);
            vos.write(bytes);
        } catch (IOException ex) {
            Logger.getLogger(VertxUtil.class.getName()).log(Level.SEVERE, null, ex);
            ctx.fail(ex);
        }
        
    }
}
