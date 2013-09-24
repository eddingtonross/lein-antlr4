;;  This is a derivative work, modified by Edward Ross.
;;  The modifications are Copyright 2012 Edward Ross, and licensed
;;  upder the Apache License, Version 2.0.

(defproject lein-antlr4 "0.1.0"
  :description "Generate source code from ANTLR4 grammars in Leiningen."
  :dependencies [[org.antlr/antlr4 "4.1"]]
  :profile {:dev {:dependencies [[org.clojure/clojure "1.4.0"]]}}
 ; :url "http://github.com/alexhall/lein-antlr"
  :eval-in-leiningen true
  :license {:name "Apache Software License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"
			:distribution :repo})
