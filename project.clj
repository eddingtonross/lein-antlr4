;;  This is a derivative work, modified by Edward Ross.
;;  The modifications are Copyright 2012 Edward Ross, and licensed
;;  upder the Apache License, Version 2.0.

(defproject lein-antlr4 "0.1.1-SNAPSHOT"
  :description "Generate source code from ANTLR4 grammars in Leiningen."
  :dependencies [[org.antlr/antlr4 "4.3"]]
  :profile {:dev {:dependencies [[org.clojure/clojure "1.4.0"]]}}
  :url "http://github.com/eddingtonross/lein-antlr4"
  :eval-in-leiningen true
  :license {:name "Apache Software License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"
			:distribution :repo})
