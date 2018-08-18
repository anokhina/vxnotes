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

import ru.org.sevn.common.data.DBProperty;
import ru.org.sevn.common.data.DBTableProperty;

/*
                'CREATE TABLE IF NOT EXISTS idprefix ' +
                '  (id INTEGER NOT NULL PRIMARY KEY, ' +
                '   name TEXT);'

*/
@DBTableProperty(name = IdPrefix.TABLE_NAME)
public class IdPrefix {
    public static final String TABLE_NAME = "idprefix";
    
    public static final String FIELD_ID = "ID";
    @DBProperty(name = FIELD_ID, dtype = "INTEGER NOT NULL PRIMARY KEY")
    private long id;
    
    public static final String FIELD_NAME = "name";
    @DBProperty(name = FIELD_NAME, dtype = "TEXT")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
