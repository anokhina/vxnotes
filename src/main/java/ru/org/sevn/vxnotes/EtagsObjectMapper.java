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

public class EtagsObjectMapper implements SimpleSqliteObjectStore.ObjectMapper<Etags>{

    @Override
    public Class getObjectType() {
        return Etags.class;
    }

    @Override
    public void mapValues(Etags o, String colName, ResultSet rs) throws SQLException {
        o.setEtags(rs.getString(colName));
    }

    @Override
    public void setStatement(Etags o, String colName, int parameterIndex, PreparedStatement pstmt) throws SQLException {
        throw new UnsupportedOperationException("Not supported");
    }
    
}
