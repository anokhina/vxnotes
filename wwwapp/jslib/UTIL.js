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
var UTIL = (function() {
    var self = {
    };
    
    self.toObject = function (arr) {
        var ret = {};
        for (let i = 0; i < arr.length; ++i) {
            ret[i] = arr[i];
        }
        ret.length = arr.length;
        return ret;
    };
    
    self.namespace = function(superPackage, ns, fnObj) {
        var o = superPackage, ts = ns.split("."), t;
        while (ts.length > 1) {
            t = ts.shift();
            if (typeof o[t] === "undefined") { o[t] = {}; }
            o = o[t];
        }
        if (ts.length == 1) {
            t = ts.shift();
            if (typeof o[t] === "undefined") { 
                o[t] = fnObj;
            }
            o = o[t];
        }

        return o;
    };
    
    self.class = function(superPackage, className, extendedClass, objectFieldsMethods) {
        var ret = self.namespace(superPackage, className, function(parametersObject) {
            if (extendedClass) {
                extendedClass.call(this, parametersObject);
            }
            if (objectFieldsMethods) {
                for (var i in objectFieldsMethods) {
                    this[i] = objectFieldsMethods[i];
                }
                if (parametersObject && "constructor" in objectFieldsMethods) {
                    objectFieldsMethods.constructor.call(this, parametersObject);
                }
            }
        });
        
        if (extendedClass) {
            ret.prototype = Object.create(extendedClass.prototype);
            ret.prototype.constructor = ret;
        }
        
        return ret;
    };
    
    var __escape = document.createElement('textarea');
    self.escapeHTML = function(html) {
        __escape.textContent = html;
        return __escape.innerHTML;
    };

    self.unescapeHTML = function (html) {
        __escape.innerHTML = html;
        return __escape.textContent;
    };
    
    self.shortStr = function(str, maxLen, strExtra) {
        if (maxLen > 0 && str && str.length > maxLen) {
            return str.substr(0, Math.min(str.length, maxLen)) + strExtra;
        }
        return str;
    };
    
    self.toEmpty = function(o) {
        if (o === null || o === undefined) {
            return "";
        }
        return o;
    };

    self.localDateString = function(d) {
        var tzoffset = d.getTimezoneOffset() * 60000;
        return (new Date(d - tzoffset)).toISOString().slice(0, -1);
    };
    
    self.localDateString2Date = function(ds) {
        var d = new Date(ds);
        var tzoffset = d.getTimezoneOffset() * 60000;
        return (new Date(d + tzoffset));
    };

    self.parseDate = function (str) {
        if (str) {
            str = str.trim();
            var d = new Date(str + " UTC"), ds;
            if(/^(\d){8}$/.test(str)) {
                var yy = str.substr(0,4),
                    mm = str.substr(4,2) - 1,
                    dd = str.substr(6,2);
                d = new Date(Date.UTC(yy,mm,dd));
            }
            try {
                return d.toISOString().slice(0,10);
            } catch (e) {
            }
        }
        return null;
    };
    
    self.tokenize = function (s, esc, sep) {
        for (var a=[], t='', i=0, e=s.length; i<e; i++) {
            var c = s.charAt(i);
            if (c == esc) { t+=s.charAt(++i); }
            else if (c != sep) { t+=c }
            else { a.push(t); t=''; }
        }
        a.push(t);
        return a;
    };
    
    self.generateGuid = function() {
        return Math.random().toString(36).substring(2, 15) +
            Math.random().toString(36).substring(2, 15);
    };
    
    self.isFunction = function(v) {
        return (v && typeof v === "function");
    };
    
    self.date2text = function(d) {
        return d.toISOString().replace(/[T\.Z]/g, "_");
    };
    
    self.objDB2text = function(obj) {
        var text = JSON.stringify( obj, null, 2 );
        var obj1 = JSON.parse(text);
        obj1.length = obj.length;
        return JSON.stringify( obj1, null, 2 );
    };
    
    self.waitUntil = function(check,onComplete,delay,timeout) {
        if (check()) {
            onComplete();
        } else {
            if (!delay) delay=100;

            var timeoutPointer;
            var intervalPointer=setInterval(function () {
                if (check()) {
                    clearInterval(intervalPointer);
                    if (timeoutPointer) clearTimeout(timeoutPointer);
                    
                    onComplete();
                }
            },delay);
            
            if (timeout) timeoutPointer=setTimeout(function () {
                clearInterval(intervalPointer);
            },timeout);
        }
    };
    
    // sleep(100).then(function(){})
    // sleep(100).then(() => {})
    self.sleep = function(ms) {
      //return new Promise((resolve, reject) => { setTimeout(resolve, ms); });
      return new Promise(resolve => setTimeout(resolve, ms));
    }
    
    return self;
})();
