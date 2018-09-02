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

import java.util.Date;
import ru.org.sevn.common.data.DBProperty;
import ru.org.sevn.common.data.DBTableProperty;
import ru.org.sevn.common.data.SimpleSqliteObjectStore;

/*
                'CREATE TABLE IF NOT EXISTS events ' +
                '  (id TEXT NOT NULL PRIMARY KEY, ' +
                '   edate DATE NOT NULL,'  +
                '   elen INTEGER NOT NULL,'  +
                '   ename TEXT NOT NULL,' +
                '   etag INTEGER,' +
    // 0 deleted
    // 1 new
    // 2 changed
    // 3 synced
                '   stat INTEGER DEFAULT 1, ' +
                '   stattime TIMESTAMP DEFAULT current_timestamp, ' +
                '   etags TEXT, ' +
                '   eurl TEXT, ' +
                '   ememo TEXT);'

*/

@DBTableProperty(name = Events.TABLE_NAME)
public class Events {
    public static final String TABLE_NAME = "events";
    
    public static final String FIELD_RID = SimpleSqliteObjectStore.ROW_ID;
    @DBProperty(name = FIELD_RID, dtype = "INTEGER PRIMARY KEY  AUTOINCREMENT   NOT NULL")
    private long rid;

    public static final String FIELD_ID = "id";
    @DBProperty(name = FIELD_ID, dtype = "TEXT NOT NULL UNIQUE")
    private String id;
    
    public static final String FIELD_EDATE = "edate";
    @DBProperty(name = FIELD_EDATE, dtype = "DATE NOT NULL")
    private String edate;
    
    public static final String FIELD_ELEN = "elen";
    @DBProperty(name = FIELD_ELEN, dtype = "INTEGER NOT NULL")
    private int elen;
    
    public static final String FIELD_ENAME = "ename";
    @DBProperty(name = FIELD_ENAME, dtype = "TEXT NOT NULL")
    private String ename;
    
    public static final String FIELD_ETAG = "etag";
    @DBProperty(name = FIELD_ETAG, dtype = "INTEGER")
    private int etag;
    
    public static final String FIELD_STAT = "stat";
    @DBProperty(name = FIELD_STAT, dtype = "INTEGER DEFAULT 1")
    private int stat = 1;
    
    public static final String FIELD_STAT_TIME = "stattime";
    @DBProperty(name = FIELD_STAT_TIME, dtype = "TIMESTAMP DEFAULT current_timestamp")
    private Date stattime = new Date();
    
    public static final String FIELD_ETAGS = "etags";
    @DBProperty(name = FIELD_ETAGS, dtype = "TEXT")
    private String etags;
    
    public static final String FIELD_EURL = "eurl";
    @DBProperty(name = FIELD_EURL, dtype = "TEXT")
    private String eurl;
    
    public static final String FIELD_EMEMO = "ememo";
    @DBProperty(name = FIELD_EMEMO, dtype = "TEXT")
    private String ememo;

    private String dlen;

    public String getDlen() {
        return dlen;
    }

    public void setDlen(String dlen) {
        this.dlen = dlen;
    }
    
    
    public long getRid() {
        return rid;
    }

    public Events setRid(long rid) {
        this.rid = rid;
        return this;
    }

    public String getId() {
        return id;
    }

    public Events setId(String id) {
        this.id = id;
        return this;
    }

    public String getEdate() {
        return edate;
    }

    public Events setEdate(String edate) {
        this.edate = edate;
        return this;
    }

    public int getElen() {
        return elen;
    }

    public Events setElen(int elen) {
        this.elen = elen;
        return this;
    }

    public String getEname() {
        return ename;
    }

    public Events setEname(String ename) {
        this.ename = ename;
        return this;
    }

    public int getEtag() {
        return etag;
    }

    public Events setEtag(int etag) {
        this.etag = etag;
        return this;
    }

    public int getStat() {
        return stat;
    }

    public Events setStat(int stat) {
        this.stat = stat;
        return this;
    }

    public Date getStattime() {
        return stattime;
    }

    public Events setStattime(Date stattime) {
        this.stattime = stattime;
        return this;
    }

    public String getEtags() {
        return etags;
    }

    public Events setEtags(String etags) {
        this.etags = etags;
        return this;
    }

    public String getEurl() {
        return eurl;
    }

    public Events setEurl(String eurl) {
        this.eurl = eurl;
        return this;
    }

    public String getEmemo() {
        return ememo;
    }

    public Events setEmemo(String ememo) {
        this.ememo = ememo;
        return this;
    }
    
    
}
