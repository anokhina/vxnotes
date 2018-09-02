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
package ru.org.sevn.common;

import java.io.File;

public abstract class AbstractSettings {
    
    private final String configDir;
    
    public AbstractSettings() {
        this("ru.org.sevn");
    }
    
    public AbstractSettings(final String configDir) {
        this.configDir = configDir;
    }
    
    public abstract File getHome();
    
    public File getConfigDir() {
        return new File(getHome(), configDir);
    }
    
    public File getAppConfigDir(String appName) {
        return new File(getConfigDir(), appName);
    }
    
    public File getAppConfigFile(String appName, String fileName) {
        return new File(mkdirs(getAppConfigDir(appName)), fileName);
    }
    
    public static File mkdirs(File f) {
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }
    
}
