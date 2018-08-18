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

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.org.sevn.common.data.SimpleSqliteObjectStore;

public class NoteService {
    
    /*
SELECT count(*) AS cnt FROM (WITH jt1 AS 
(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( 
 SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( 
 SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events 
 WHERE stat != 0 AND coalesce(eurl, '') = '' and 
 ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? 
 UNION ALL 
 SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen 
  FROM events t1 
  JOIN events t2 
    ON (t1.ename = t2.ename) 
 WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? 
 ) GROUP BY ename, l 
 )) 
 SELECT * FROM jt1 
 UNION ALL 
 SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events 
 WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? 
 ORDER BY edate DESC, ename ASC 
)    
    */
    final static String COUNTLINES = "SELECT count(*) AS cnt FROM (WITH jt1 AS \n" +
"(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( \n" +
" SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events \n" +
" WHERE stat != 0 AND coalesce(eurl, '') = '' and \n" +
" ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? \n" +
" UNION ALL \n" +
" SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen \n" +
"  FROM events t1 \n" +
"  JOIN events t2 \n" +
"    ON (t1.ename = t2.ename) \n" +
" WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? \n" +
" ) GROUP BY ename, l \n" +
" )) \n" +
" SELECT * FROM jt1 \n" +
" UNION ALL \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events \n" +
" WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? \n" +
" ORDER BY edate DESC, ename ASC \n" +
")";
    //[ "2018-08-15", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15" ]
    /*
WITH jt1 AS 
(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( 
 SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( 
 SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events 
 WHERE stat != 0 AND coalesce(eurl, '') = '' and 
 ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? 
 UNION ALL 
 SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen 
  FROM events t1 
  JOIN events t2 
    ON (t1.ename = t2.ename) 
 WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? 
 ) GROUP BY ename, l 
 )) 
 SELECT * FROM jt1 
 UNION ALL 
 SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events 
 WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? 
 ORDER BY edate DESC, ename ASC 
 LIMIT ? OFFSET ?     
    */
    //[ "2018-08-15", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", 30, 0 ]
    final static String LINES = "WITH jt1 AS \n" +
"(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( \n" +
" SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events \n" +
" WHERE stat != 0 AND coalesce(eurl, '') = '' and \n" +
" ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? \n" +
" UNION ALL \n" +
" SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen \n" +
"  FROM events t1 \n" +
"  JOIN events t2 \n" +
"    ON (t1.ename = t2.ename) \n" +
" WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? \n" +
" ) GROUP BY ename, l \n" +
" )) \n" +
" SELECT * FROM jt1 \n" +
" UNION ALL \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events \n" +
" WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? \n" +
" ORDER BY edate DESC, ename ASC \n" +
" LIMIT ? OFFSET ? ";
    /*
    INSERT INTO events (id, edate, ename, ememo, elen, etag, etags, eurl) VALUES (?,?,?,?,?, ?,?,?)
    */
    //[ "vrtx-2nc2xg21v0qw00xhawl8ki", "2018-08-15", "Apache Cordova Tutorial", "", "1", "", "test", "https://ccoenraets.github.io/cordova-tutorial/single-page-app.html" ]
    final static String ADD_REC = "INSERT INTO events (id, edate, ename, ememo, elen, etag, etags, eurl) VALUES (?,?,?,?,?, ?,?,?)";
    //[ "2018-08-14", "Apache Cordova Tutorial", "some notes", "1", "0", "testa", "https://ccoenraets.github.io/cordova-tutorial/single-page-app.html", "vrtx-zxkvciuswio86y6kxxgrm" ]
    final static String CHANGE_REC = "UPDATE events SET edate = ?, ename = ?, ememo = ?, elen = ?, etag = ?, etags = ?, eurl = ?, stat = 2, stattime=(datetime('now')) WHERE id = ? ";

    final static String TAGS = "SELECT DISTINCT etags FROM events ORDER BY etags";
    
    /*
SELECT count(*) AS cnt FROM (WITH jt1 AS 
(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( 
 SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( 
 SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events 
 WHERE stat != 0 AND coalesce(eurl, '') = '' and  etags LIKE ? 
 AND 
 ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? 
 UNION ALL 
 SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen 
  FROM events t1 
  JOIN events t2 
    ON (t1.ename = t2.ename) AND  t2.etags LIKE ? 
 WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? 
 ) GROUP BY ename, l 
 )) 
 SELECT * FROM jt1 
 UNION ALL 
 SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events 
 WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? AND  etags LIKE ? 
 ORDER BY edate DESC, ename ASC 
)    
    */
    //[ "2018-08-15", "test", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "test", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "test" ]
    final static String BY_TAG_CNT = "SELECT count(*) AS cnt FROM (WITH jt1 AS \n" +
"(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( \n" +
" SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events \n" +
" WHERE stat != 0 AND coalesce(eurl, '') = '' and  etags LIKE ? \n" +
" AND \n" +
" ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? \n" +
" UNION ALL \n" +
" SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen \n" +
"  FROM events t1 \n" +
"  JOIN events t2 \n" +
"    ON (t1.ename = t2.ename) AND  t2.etags LIKE ? \n" +
" WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? \n" +
" ) GROUP BY ename, l \n" +
" )) \n" +
" SELECT * FROM jt1 \n" +
" UNION ALL \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events \n" +
" WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? AND  etags LIKE ? \n" +
" ORDER BY edate DESC, ename ASC \n" +
")";
    
    /*
WITH jt1 AS 
(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( 
 SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( 
 SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events 
 WHERE stat != 0 AND coalesce(eurl, '') = '' and  etags LIKE ? 
 AND 
 ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? 
 UNION ALL 
 SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen 
  FROM events t1 
  JOIN events t2 
    ON (t1.ename = t2.ename) AND  t2.etags LIKE ? 
 WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? 
 ) GROUP BY ename, l 
 )) 
 SELECT * FROM jt1 
 UNION ALL 
 SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events 
 WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? AND  etags LIKE ? 
 ORDER BY edate DESC, ename ASC 
 LIMIT ? OFFSET ?     
    */
    //[ "2018-08-15", "test", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "test", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "test", 30, 0 ]
    final static String BY_TAG = "WITH jt1 AS \n" +
"(SELECT etag, ename, ememo, etags, eurl, id, elen, l AS pdate, r AS edate, CAST ((julianday(r)-julianday(l)) AS INTEGER) AS dlen FROM ( \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, l, min(r) AS r FROM ( \n" +
" SELECT '' AS etag, ename, edate AS l, ? AS r , 'FAKE' AS ememo, '' as etags, '' as eurl, '' as id, '' AS elen FROM events \n" +
" WHERE stat != 0 AND coalesce(eurl, '') = '' and  etags LIKE ? \n" +
" AND \n" +
" ? >= ? AND ? <= ? AND edate < ? AND edate >= ? AND edate <= ? \n" +
" UNION ALL \n" +
" SELECT t2.etag, t1.ename, t1.edate AS l, t2.edate AS r, t2.ememo, t2.etags, t2.eurl, t2.id, t2.elen \n" +
"  FROM events t1 \n" +
"  JOIN events t2 \n" +
"    ON (t1.ename = t2.ename) AND  t2.etags LIKE ? \n" +
" WHERE t1.stat != 0 AND t2.stat != 0 AND t1.edate < t2.edate AND t1.edate >= ? AND t2.edate <= ? \n" +
" ) GROUP BY ename, l \n" +
" )) \n" +
" SELECT * FROM jt1 \n" +
" UNION ALL \n" +
" SELECT etag, ename, ememo, etags, eurl, id, elen, '' AS pdate, edate, 0 AS dlen FROM events \n" +
" WHERE stat != 0 AND id NOT IN (SELECT id FROM jt1) AND edate BETWEEN ? AND ? AND  etags LIKE ? \n" +
" ORDER BY edate DESC, ename ASC \n" +
" LIMIT ? OFFSET ? ";

    private final SimpleSqliteObjectStore store;
    
    public NoteService(final SimpleSqliteObjectStore s) {
        this.store = s;
    }
    /*
{
  "status": "success",
  "data": {
    
  },
  "message": null 
}    
    */
    //https://stackoverflow.com/questions/44655097/generate-swagger-api-with-existing-vert-x-project
    //https://github.com/outofcoffee/vertx-oas
    //https://github.com/conorroche/swagger-doclet
    
    //https://github.com/slinkydeveloper/vertx-web/blob/designdriven/vertx-web/src/main/asciidoc/java/index.adoc#validate-the-requests
    //http://jsonapi.org/format/#errors
    //https://github.com/slinkydeveloper/vertx-web/blob/designdriven/vertx-web-api-contract/vertx-web-api-contract-openapi/src/main/asciidoc/java/index.adoc
    public JsonObject execute(final String sql, final JsonArray args) {
        if (sql != null) {
            switch(sql) {
                case "SELECT name FROM idprefix WHERE id = 1": 
                {
                    final JsonArray rows = new JsonArray();
                    rows.add(new JsonObject().put("name", "vrtx"));
                    return new JsonObject().put("data", new JsonObject().put("rows", rows));
                }
                case "CREATE TABLE IF NOT EXISTS events   (id TEXT NOT NULL PRIMARY KEY,    edate DATE NOT NULL,   elen INTEGER NOT NULL,   ename TEXT NOT NULL,   etag INTEGER,   stat INTEGER DEFAULT 1,    stattime TIMESTAMP DEFAULT current_timestamp,    etags TEXT,    eurl TEXT,    ememo TEXT);":
                {
                    return new JsonObject().put("data", new JsonObject().put("rows", new JsonArray()));
                }
                case "CREATE TABLE IF NOT EXISTS idprefix   (id INTEGER NOT NULL PRIMARY KEY,    name TEXT);":
                {
                    return new JsonObject().put("data", new JsonObject().put("rows", new JsonArray()));
                }
                case ADD_REC:
                {
                    //id, edate, ename, ememo, elen, etag, etags, eurl
                    int i = 0;
                    final Events event = new Events().setId(args.getString(i++))
                            .setEdate(args.getString(i++))
                            .setEname(args.getString(i++))
                            .setEmemo(args.getString(i++))
                            .setElen(Util.toInt(args.getString(i++)))
                            .setEtag(Util.toInt(args.getString(i++)))
                            .setEtags(args.getString(i++))
                            .setEurl(args.getString(i++));
                    System.out.println("******" + Json.encodePrettily(event));
                    final int added = store.addObject(event);
                    if (added > 0) {
                        return new JsonObject().put("rowsAffected", added);
                    } else {
                        return new JsonObject().put("errors", new JsonArray().add(
                                new JsonObject()
                                .put("title", "Can't add object")));
                    }
                }
                case CHANGE_REC://"UPDATE events SET edate = ?, ename = ?, ememo = ?, elen = ?, etag = ?, etags = ?, eurl = ?, stat = 2, stattime=(datetime('now')) WHERE id = ? ";
                {
                    
                    Collection<Events> objects;
                    try {
                        objects = store.getObjects(Events.class, new String[] { Events.FIELD_ID }, new Object[] { args.getString(args.size() - 1) });
                    } catch (Exception ex) {
                        Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
                        return new JsonObject().put("errors", new JsonArray().add(
                                new JsonObject()
                                .put("title", "Can't change object")));
                    }
                    int i = 0;
                    for (Events event : objects) {
                        event
                            .setEdate(args.getString(i++))
                            .setEname(args.getString(i++))
                            .setEmemo(args.getString(i++))
                            .setElen(Util.toInt(args.getString(i++)))
                            .setEtag(Util.toInt(args.getString(i++)))
                            .setEtags(args.getString(i++))
                            .setEurl(args.getString(i++))
                            .setStat(2)
                            .setStattime(new Date())
                                ;
                        System.out.println("******" + Json.encodePrettily(event));
                        final int changed = store.updateObject(event);
                        if (changed > 0) {
                            return new JsonObject().put("rowsAffected", changed);
                        } else {
                            return new JsonObject().put("errors", new JsonArray().add(
                                    new JsonObject()
                                    .put("title", "Can't change object")));
                        }
                    }
                }
                case "SELECT * FROM events WHERE id = ?":
                {
                    final Object[] val = new Object[args.size()];
                    int i = 0;
                    for (; i < args.size(); i++) {
                        val[i] = args.getString(i);
                    }
                    try {
                        JsonArray rows = new JsonArray();
                        for (final Object o : store.getObjects(null, Events.class, "SELECT * FROM events WHERE id = ?", new String[val.length], val, null, null)) {
                            rows.add(JsonObject.mapFrom(o));
                        }
                        return new JsonObject().put("data", new JsonObject().put("rows", rows));
                    } catch (Exception ex) {
                        Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
                        return new JsonObject().put("errors", new JsonArray().add(
                                new JsonObject()
                                .put("title", "sql error")));
                    }
                }
                case COUNTLINES:
                {
                    //[ "2018-08-15", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15" ]
                    final Object[] val = new Object[args.size()];
                    int i = 0;
                    for (; i < args.size(); i++) {
                        val[i] = Util.toDate(args.getString(i));
                    }
                    try {
                        JsonArray rows = new JsonArray();
                        for (final Object o : store.getObjects(new SimpleSqliteObjectStore.CountObjectMapper(), Events.class, COUNTLINES, new String[val.length], val, null, null)) {
                            rows.add(JsonObject.mapFrom(o));
                        }
                        return new JsonObject().put("data", new JsonObject().put("rows", rows));
                    } catch (Exception ex) {
                        Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
                        return new JsonObject().put("errors", new JsonArray().add(
                                new JsonObject()
                                .put("title", "sql error")));
                    }
                }
                case LINES:
                {
                    //[ "2018-08-15", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", 30, 0 ]
                    final Object[] val = new Object[args.size()];
                    int i = 0;
                    for (; i < args.size() - 2; i++) {
                        val[i] = Util.toDate(args.getString(i));
                    }
                    val[i] = args.getInteger(i);
                    i++;
                    val[i] = args.getInteger(i);
                    try {
                        JsonArray rows = new JsonArray();
                        for (final Object o : store.getObjects(null, Events.class, LINES, new String[val.length], val, null, null)) {
                            rows.add(JsonObject.mapFrom(o));
                        }
                        return new JsonObject().put("data", new JsonObject().put("rows", rows));
                    } catch (Exception ex) {
                        Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
                        return new JsonObject().put("errors", new JsonArray().add(
                                new JsonObject()
                                .put("title", "sql error")));
                    }
                }
                case TAGS:
                {
                    try {
                        JsonArray rows = new JsonArray();
                        for (final Object o : store.getObjects(new EtagsObjectMapper(), Events.class, TAGS, new String[0], new Object[0], null, null)) {
                            rows.add(JsonObject.mapFrom(o));
                        }
                        return new JsonObject().put("data", new JsonObject().put("rows", rows));
                    } catch (Exception ex) {
                        Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
                        return new JsonObject().put("errors", new JsonArray().add(
                                new JsonObject()
                                .put("title", "sql error")));
                    }
                }
                case BY_TAG_CNT:
                {
                    return byTagCount(BY_TAG_CNT, args);
                }
                case BY_TAG:
                {
                    return byTag(BY_TAG, args);
                }
            }
        }
        JsonObject ret = trybyTag(sql, args);
        if (ret != null) {
            return ret;
        }
        ret = trybyTagCount(sql, args);
        if (ret != null) {
            return ret;
        }
        return new JsonObject().put("errors", new JsonArray().add(
                new JsonObject().put("id", "1234567")
                .put("title", "err msg")
                //
        ));
    }
    
    private JsonObject byTagCount(final String byTagSql, final JsonArray args) {
        //[ "2018-08-15", "test", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "test", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "test" ]
        final Object[] val = new Object[args.size()];
        int i = 0;
        for (; i < args.size(); i++) {
            if (i == 1 || i == 9 || i == 14) {
                val[i] = args.getString(i);
            } else {
                val[i] = Util.toDate(args.getString(i));
            }
        }
        try {
            JsonArray rows = new JsonArray();
            for (final Object o : store.getObjects(new SimpleSqliteObjectStore.CountObjectMapper(), Events.class, byTagSql, new String[val.length], val, null, null)) {
                rows.add(JsonObject.mapFrom(o));
            }
            return new JsonObject().put("data", new JsonObject().put("rows", rows));
        } catch (Exception ex) {
            Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
            return new JsonObject().put("errors", new JsonArray().add(
                    new JsonObject()
                    .put("title", "sql error")));
        }
    }
    private final String[] STRING_FIELDS = new String[] {"ename", "ememo", "eurl", "elen"};
    private JsonObject trybyTagCount(final String sql, final JsonArray args) {
        if (sql.startsWith(BY_TAG_CNT.substring(0, 15))) {
            for (final String v : STRING_FIELDS) {
                final String byTagSql = BY_TAG_CNT.replace("etags LIKE", v + " LIKE");
                if (byTagSql.trim().equals(sql.trim())) {
                    return byTagCount(byTagSql, args);
                }
            }
        }
        return null;
    }
    private JsonObject trybyTag(final String sql, final JsonArray args) {
        if (sql.startsWith(BY_TAG.substring(0, 15))) {
            for (final String v : STRING_FIELDS) {
                final String byTagSql = BY_TAG.replace("etags LIKE", v + " LIKE");
//                System.out.println("1>"+byTagSql);
//                System.out.println("2>"+sql);
                if (byTagSql.trim().equals(sql.trim())) {
                    return byTag(byTagSql, args);
                }
            }
        }
        return null;
    }
    //etags LIKE
    private JsonObject byTag(final String byTagSql, final JsonArray args) {
        //[ "2018-08-15", "test", "2018-08-15", "1970-01-01", "2018-08-15", "2118-08-15", "2018-08-15", "1970-01-01", "2118-08-15", "test", "1970-01-01", "2118-08-15", "1970-01-01", "2118-08-15", "test", 30, 0 ]
        final Object[] val = new Object[args.size()];
        int i = 0;
        for (; i < args.size() - 2; i++) {
            if (i == 1 || i == 9 || i == 14) {
                val[i] = args.getString(i);
            } else {
                val[i] = Util.toDate(args.getString(i));
            }
        }
        val[i] = args.getInteger(i);
        i++;
        val[i] = args.getInteger(i);
        try {
            JsonArray rows = new JsonArray();
            for (final Object o : store.getObjects(null, Events.class, byTagSql, new String[val.length], val, null, null)) {
                rows.add(JsonObject.mapFrom(o));
            }
            return new JsonObject().put("data", new JsonObject().put("rows", rows));
        } catch (Exception ex) {
            Logger.getLogger(NoteService.class.getName()).log(Level.SEVERE, null, ex);
            return new JsonObject().put("errors", new JsonArray().add(
                    new JsonObject()
                    .put("title", "sql error")));
        }
    }
}


    /*
    tx.executeSql("UPDATE events SET edate = ?, ename = ?, ememo = ?, elen = ?, etag = ?, etags = ?, eurl = ?, stat = 3, stattime=(datetime('now')) WHERE id = ? AND stattime < ?",
    [ evt.edate, evt.ename, evt.ememo, evt.elen, evt.etag, etags2(evt.etags), evt.eurl, evt.id, evt.stattime],
     */
    /*
    tx.executeSql("UPDATE events SET etag = ?, stat = 2, stattime=(datetime('now')) WHERE id = ? ", [ evt.etag, evt.id],
     */
    /*
    tx.executeSql("UPDATE events SET stat = 3, stattime=(datetime('now')) WHERE stat in (1, 2) ", [ ],
     */
    /*
    tx.executeSql('DELETE FROM events WHERE stat = 0 ', [],
     */
    /*
    tx.executeSql("UPDATE events SET stat = 0, stattime=(datetime('now')) WHERE id = ? ", [ id],
     */
    
