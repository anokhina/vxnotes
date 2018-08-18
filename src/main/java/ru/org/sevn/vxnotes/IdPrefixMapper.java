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

public class IdPrefixMapper implements SimpleSqliteObjectStore.ObjectMapper<IdPrefix>{

    @Override
    public Class getObjectType() {
        return IdPrefix.class;
    }

    @Override
    public void mapValues(IdPrefix o, String colName, ResultSet rs) throws SQLException {
        switch(colName) {
            case IdPrefix.FIELD_ID:
                o.setId(rs.getLong(colName));
                break;
            case IdPrefix.FIELD_NAME:
                o.setName(rs.getString(colName));
                break;
        }
    }

    @Override
    public void setStatement(IdPrefix o, String colName, int parameterIndex, PreparedStatement pstmt) throws SQLException {
        switch(colName) {
            case IdPrefix.FIELD_ID:
                pstmt.setLong(parameterIndex, o.getId());
                break;
            case IdPrefix.FIELD_NAME:
                pstmt.setString(parameterIndex, o.getName());
        }
    }
    
}
