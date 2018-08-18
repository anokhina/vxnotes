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
window.URL = window.URL || window.webkitURL;

var UTILIO = (function() {
    var self = {
    };
    
    self.text2blob = function(text) {
        return new Blob([text], {type: "text/plain;charset=utf-8"});
    };
    
    self.saveAs = function(blob, filename) {
        if (window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveBlob(blob, filename);
        } else {
            var elem = window.document.createElement('a');
            elem.href = window.URL.createObjectURL(blob);
            elem.download = filename;        
            document.body.appendChild(elem);
            elem.click();
            document.body.removeChild(elem);
        }
    };
    
    self.saveXUrlAs = function(srcurl, filename) {

        var xhr = new XMLHttpRequest(),
              a = document.createElement('a');

        xhr.open('GET', srcurl, true);
        xhr.responseType = 'blob';
        xhr.onload = function () {
            var file = new Blob([xhr.response], { type : 'application/octet-stream' });
            a.href = window.URL.createObjectURL(file);
            a.download = filename;
            a.click();
            document.body.removeChild(a);
        };
        xhr.send();        
    }

    self.saveUrlAs = function(srcurl, filename) {
        var elem = window.document.createElement('a');
        elem.href = srcurl;
        elem.download = filename;        
        document.body.appendChild(elem);
        elem.click();        
        document.body.removeChild(elem);
    };
    
    UTIL.class(self, "FileOpener", null, {
        readFile : function(f) {
            console.log("read file:", f);
        },
        handleFileSelect: function(evt) {
            if (window.File && window.FileReader && window.FileList && window.Blob) {

            } else {
                alert('The File APIs are not fully supported in this browser.');
                return;
            }

            for (var i = 0, f; f = evt.target.files[i]; i++) {
                this.readFile(f);
            }
        }
    });
    UTIL.class(self, "FileOpenerText", self.FileOpener, {
        readAsText: function(f) {
            var me = this;
            var reader = new FileReader();
            reader.readAsText (f);
            reader.onload = function(e) {
                me.textProcessor(reader.result, f);
            };
        },
        readFile: function(f) {
            this.readAsText(f);
        },
        constructor: function(textProcessorFn) {
            this.textProcessor = textProcessorFn;
        }
    });
    
    self.handleURLFiles = function(files, callback/*(fileContentArray)*/) {
        self.handleFileArray(files, self.text2url, callback);
    }
    
    self.handleFileArray = function(files, text2Object/*(txt, fl)*/, callback/*(fileContentArray)*/) {
        var ctx = {fileContent: [], maxLen: files.length };
        
        for (var i = 0, f; f = files[i]; i++) {
            var reader = new FileReader();
            (function(fl, reader, ctx) {
                reader.onload = function(e) {
                    ctx.fileContent.push({file: fl, content: text2Object(reader.result, fl)});
                    if (ctx.fileContent.length == ctx.maxLen) {
                        if (callback) callback(ctx.fileContent);
                    }
                };
                reader.onerror = function(e) {
                    ctx.fileContent.push({file: fl, content: null});
                }
            })(f, reader, ctx);
            reader.readAsText (f);
        }
    };

    self.text2url = function(txt, fl) {
        var urlObj = {};
        if (fl) {
            urlObj.title = fl.name;
        }
        
        var lines = txt.match(/[^\r\n]+/g);
        if (lines == null) {
            lines = [txt];
        }
        for (var j = 0; j < lines.length; j++) {
            if (lines[j].startsWith("URL=")) {
                urlObj.url = lines[j].substring(4);
            } else 
            if (lines[j].startsWith("Comment=")) {
                urlObj.comment = lines[j].substring(8);
            } 
        }
        return urlObj;
    };
    
    self.handleURLDnD = function(evt, callback) {
        evt.stopPropagation();
        evt.preventDefault();
        var files = evt.dataTransfer.files;
        var urls = [];
        var output = [];
        if (files.length > 0) {
            self.handleURLFiles(files, callback);
        } else {
            if (callback && evt.dataTransfer.getData("text/uri-list")) {
                callback([{content: {title: "Untitled", url: evt.dataTransfer.getData("text/uri-list")}}])
            }
        }
        
    };
    
    self.getFileTitle = function(txt, exts) {
        if (txt) {
            var lowTxt = txt.toLowerCase();
            for (var i = 0; i < exts.length; i++) {
                var ext = exts[i];
                if (lowTxt.endsWith(ext)) {
                    return txt.substring(0, txt.length - ext.length);
                }
            }
        }
        return txt;
    };
    
    return self;
})();
