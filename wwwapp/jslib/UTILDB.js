/*
Copyright 2017 Veronica Anokhina.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
var UTILDB = (function() {
    var self = {
    };

    self.dropTables = function(db, names) {
        db.transaction(
            function(transaction) {
                names.forEach(function(name) {
                    transaction.executeSql(
                        'DROP TABLE IF EXISTS ' + name
                    );
                });
            }
        );
    };

    self.dropViews = function(db, names) {
        db.transaction(
            function(transaction) {
                names.forEach(function(name) {
                    transaction.executeSql(
                        'DROP VIEW IF EXISTS ' + name
                    );
                });
            }
        );
    };
    
    self.showTable = function(db, table) {
        db.transaction(function (tx) {
            tx.executeSql('SELECT ROWID, * FROM ' + table, [], 
                function (tx, results) {
                    var len = results.rows.length;
                    var msg = "<p>Found rows: " + len + "</p>";
                    console.log(msg);
            
                    for (var i = 0; i < len; i++) {
                        console.log(i, results.rows.item(i));
                    }
                },
                function(err) { 
                    console.log(err);
                }
            );
        });
    };
    
    return self;
})();
