;;  Copyright 2010 Revelytix, Inc.
;;
;;  Licensed under the Apache License, Version 2.0 (the "License");
;;  you may not use this file except in compliance with the License.
;;  You may obtain a copy of the License at
;;
;;      http://www.apache.org/licenses/LICENSE-2.0
;;
;;  Unless required by applicable law or agreed to in writing, software
;;  distributed under the License is distributed on an "AS IS" BASIS,
;;  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;;  See the License for the specific language governing permissions and
;;  limitations under the License.
;;
;;  This is a derivative work, modified by Edward Ross.
;;  The modifications are Copyright 2012 Edward Ross, and licensed
;;  upder the Apache License, Version 2.0.


(ns leiningen.test-antlr4
  (:use [leiningen.antlr4 :only (antlr4)]
        [leiningen.clean :only (delete-file-recursively)]
        [clojure.test])
  (:import [java.io File]))

(def antlr-src-dir "antlr")
(def antlr-out-dir "antlr-out")

(defn antlr-project [d]
  {:antlr-src-dir (str antlr-src-dir \/ d)
   :antlr-dest-dir (str antlr-out-dir \/ d)})

(when (.exists (File. antlr-out-dir)) (delete-file-recursively antlr-out-dir false))

(defn out-file [f] (File. antlr-out-dir f))
(defn out-file-exists [f] (.exists (out-file f)))

(deftest test-antlr-compile
  (let [result (with-out-str (antlr4 (antlr-project "test")))]
    (is (true? (.startsWith result "Compiling ANTLR grammars"))))
  (are [x] (true? (out-file-exists x))
    "test/antlr/test/SimpleCalc.tokens"
    "test/antlr/test/SimpleCalcLexer.java"
    "test/antlr/test/SimpleCalcParser.java"
    "test/paren/antlr/test/paren/ParenCalc.tokens"
    "test/paren/antlr/test/paren/ParenCalcLexer.java"
    "test/paren/antlr/test/paren/ParenCalcParser.java")
  (let [timestamp (.lastModified (out-file "test/SimpleCalcLexer.java"))]
    (Thread/sleep 2000)
    (antlr4 (antlr-project "test"))
    (is (= timestamp (.lastModified (out-file "test/SimpleCalcLexer.java"))))))

(deftest test-antlr-invalid
  (is (thrown? RuntimeException (antlr4 (antlr-project "test-invalid"))))
  (is (false? (out-file-exists "test-invalid/antlr/test-invalid/InvalidCalcLexer.java"))))

(deftest test-suffix
  (antlr4 (antlr-project "test-suffix"))
  (are [x] (true? (out-file-exists x))
    "test-suffix/antlr/test-suffix/SimpleCalc.tokens"
    "test-suffix/antlr/test-suffix/SimpleCalcLexer.java"
    "test-suffix/antlr/test-suffix/SimpleCalcParser.java"))
