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

import java.io.File;
import java.util.HashMap;
import ru.org.sevn.common.FileUtil;
import ru.org.sevn.common.SevnSettings;
import ru.org.sevn.common.data.SimpleSqliteObjectStore;

public class DBManager {
    private final static HashMap<String, SimpleSqliteObjectStore> map = new HashMap<>();
    
    public static SimpleSqliteObjectStore getSimpleSqliteObjectStore(final String userName, final String dbVariant) {
        final String dbvar = getDbVariant(dbVariant);
        SimpleSqliteObjectStore ret;
        synchronized(map) {
            ret = map.get(userName + dbvar);
            if (ret == null) {
                final File dbDir = SevnSettings.mkdirs(new SevnSettings().getAppConfigFile("vxnotes", "db"));
                ret = new SimpleSqliteObjectStore(new File(dbDir, FileUtil.replaceForbidden(userName.trim()) + dbvar + "-events.db").getAbsolutePath(), new EventsMapper());
                map.put(userName + dbvar, ret);
            }
        }
        return ret;
    }
    
    public static String getDbVariant(final String s) {
        if (s == null) {
            return "";
        }
        return "-" + FileUtil.replaceForbidden(s);
    }
}
