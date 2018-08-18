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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Util {

    public static String toStr(Date d) {
        return new Date(d.getTime()).toInstant ().atZone (ZoneId.systemDefault ()).toLocalDateTime ().format (DateTimeFormatter.ISO_DATE);
    }
    
    public static Date toDate(String str) {
        return Date.from(LocalDate.parse(str, DateTimeFormatter.ISO_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Integer toInt(String s) {
        if (s.length() == 0) {
            return 0;
        }
        return Integer.valueOf(s);
    }
}
