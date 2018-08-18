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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ru.org.sevn.common.data.SimpleSqliteObjectStore;

public class EventsMapper implements SimpleSqliteObjectStore.ObjectMapper<Events> {

    @Override
    public Class getObjectType() {
        return Events.class;
    }

    @Override
    public void mapValues(Events o, String colName, ResultSet rs) throws SQLException {
        switch(colName) {
            case Events.FIELD_RID:
                o.setRid(rs.getLong(colName));
                break;
            case Events.FIELD_ID:
                o.setId(rs.getString(colName));
                break;
            case Events.FIELD_EDATE:
                o.setEdate(Util.toStr(rs.getDate(colName)));
                break;
            case Events.FIELD_ELEN:
                o.setElen(rs.getInt(colName));
                break;
            case Events.FIELD_EMEMO:
                o.setEmemo(rs.getString(colName));
                break;
            case Events.FIELD_ENAME:
                o.setEname(rs.getString(colName));
                break;
            case Events.FIELD_ETAG:
                o.setEtag(rs.getInt(colName));
                break;
            case Events.FIELD_ETAGS:
                o.setEtags(rs.getString(colName));
                break;
            case Events.FIELD_EURL:
                o.setEurl(rs.getString(colName));
                break;
            case Events.FIELD_STAT:
                o.setStat(rs.getInt(colName));
                break;
            case Events.FIELD_STAT_TIME:
                o.setStattime(rs.getDate(colName));
                break;
        }
    }
    
    public static java.sql.Date get(java.util.Date d) {
        if (d != null) {
            return new java.sql.Date(d.getTime());
        }
        return null;
    }

    @Override
    public void setStatement(Events o, String colName, int parameterIndex, PreparedStatement pstmt) throws SQLException {
        switch(colName) {
            case Events.FIELD_RID:
                pstmt.setLong(parameterIndex, o.getRid());
                break;
            case Events.FIELD_ID:
                pstmt.setString(parameterIndex, o.getId());
                break;
            case Events.FIELD_EDATE:
                pstmt.setDate(parameterIndex, get(Util.toDate(o.getEdate())));
                break;
            case Events.FIELD_ELEN:
                pstmt.setInt(parameterIndex, o.getElen());
                break;
            case Events.FIELD_EMEMO:
                pstmt.setString(parameterIndex, o.getEmemo());
                break;
            case Events.FIELD_ENAME:
                pstmt.setString(parameterIndex, o.getEname());
                break;
            case Events.FIELD_ETAG:
                pstmt.setInt(parameterIndex, o.getEtag());
                break;
            case Events.FIELD_ETAGS:
                pstmt.setString(parameterIndex, o.getEtags());
                break;
            case Events.FIELD_EURL:
                pstmt.setString(parameterIndex, o.getEurl());
                break;
            case Events.FIELD_STAT:
                pstmt.setInt(parameterIndex, o.getStat());
                break;
            case Events.FIELD_STAT_TIME:
                pstmt.setDate(parameterIndex, get(o.getStattime()));
                break;
        }
    }
    
}
