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

(ns leiningen.antlr4
  (:use [leiningen.clean :as clean :only (clean delete-file-recursively)]
        [robert.hooke :only (add-hook)])
  (:import [java.io File FileFilter]
           [java.net URI]
           [org.antlr.v4 Tool]))

(defn sub-dirs
  "Recursively find all subdirectories under the given root directory. Also returns the root directory."
  [^File f]
  (if (.isDirectory f)
    (cons f
          (apply concat
                 (for [d (.listFiles f (proxy [FileFilter] [] (accept [child] (.isDirectory child))))]
                   (sub-dirs d))))
    '()))

(defn has-suffix?
  "Returns true if file f has one of the given suffixes as its filename extension. The suffixes should not include the '.' character."
  [suffixes f]
  (let [n (.getName f)
        idx (.lastIndexOf n ".")
        suffix (if (> idx -1) (.substring n (inc idx)))]
    (contains? suffixes suffix)))

(defn dirs-with-type
  "Recursively find all directories rooted at f that contain a file with one of the given suffixes as a direct child."
  [^File f suffixes]
  (filter (fn [^File d] (some (partial has-suffix? suffixes)
                              (.listFiles d)))
          (sub-dirs f)))

(defn files-of-type
  "List all files that are direct children of the given directory with names ending in one of the given suffixes."
  [^File dir suffixes]
  (seq (.listFiles dir (proxy [FileFilter] [] (accept [f] (has-suffix? suffixes f))))))

(defn relative-paths
  "Takes a root directory 'parent' and a seq of children within the directory, and returns a seq of
relative file: URIs that give the pathnames of the children relative to the root directory."
  [^File parent children]
  (let [^URI parent-uri (.toURI parent)]
    (for [^File child children]
      (.relativize parent-uri (.toURI child)))))

(defn absolute-files
  "Takes a root directory 'parent' and a seq of relative file: URIs (as generated by relative-paths)
and returns a seq of absolute File objects that represent those relative paths resolved against the root directory."
  [^File parent child-paths]
  (let [^URI parent-uri (URI. (str (.toURI parent) "/"))]
    (for [^URI child-path child-paths]
      (File. (.resolve parent-uri child-path)))))

(def ^{:doc "Default options for the ANTLR tool."} default-antlr-opts
  {:atn false
   :message-format "antlr"
   :listener true
   :visitor false
   :encoding "UTF-8"
   :package false
   :depend false
   :D {}
   :warn-error false
   :save-lexer false
   :debug-string-template false
   :force-atn false
   :log false
   :verbose-dfa false
   })

(defn option-text [text]
  (fn [x] (if x (str " -" text ) "")))

(defn binary-option [text]
  (fn [x] (str " -" (if x "" "no-") text)))


(def ^{:doc "Mapping of option names to functions mapping the value of the option 
            to a corresponding command line string"} opts-to-command
  {
   :atn (option-text "atn")
   :message-format #(str " -message-format " %)
   :listener (binary-option "listener")
   :visitor (binary-option "visitor")
   :encoding #(str " -encoding " %)
   :package (option-text "package")
   :depend (option-text "depend")
   :D #(apply str
              (for [[option value] %]
                (str " -D" option "=" value)))
   :warn-error (option-text "Werror")
   :save-lexer (option-text "Xsave-lexer")
   :debug-string-template (option-text "XdbgST")
   :force-atn (option-text "Xforce-atn")
   :log (option-text "Xlog")
   :verbose-dfa (option-text "Xverbose-dfa")
   })

(defn output-command [output-dir]
  (str "-o " output-dir " "))

(defn input-command [input-dir]
  (str "-lib " input-dir " "))

(defn options-command [options]
  (apply str
         (for [[option value] options]
           ((opts-to-command option) value))))

(defn files-command [files]
  (apply str (for [file files] (str " " file))))

(def ^{:doc "The collection of file extensions that ANTLR accepts (hard-coded in the ANTLR tool)."}
      file-types #{"g" "g4"})

(process-antlr-dir (File. ".") (File. ".") default-antlr-opts)

(defn process-antlr-dir
  "Processes ANTLR grammar files in the given intput directory to generate output in the given output directory
with the given configuration options."
  [^File input-dir ^File output-dir antlr-opts]
  (if-let [grammar-files (files-of-type input-dir file-types)]
    (do
      (println "Compiling ANTLR grammars:" (apply str (interpose " " (map #(.getName %) grammar-files))) "...")
;    ;; The ANTLR tool uses static state to track errors -- reset before each run.
;    (org.antlr.v4.tool.ErrorManager/resetErrorState)
    (let
      [input-string (input-command input-dir)
       output-string (output-command output-dir)
       options-string (options-command antlr-opts)
       files (files-command grammar-files)]
      (Tool. (str output-string input-string options-string files))))))
;    (if (> (.getNumErrors antlr-tool) 0)
;      (throw (RuntimeException. (str "ANTLR detected " (.getNumErrors antlr-tool) " grammar errors."))))))

(defn compile-antlr
  "Recursively process all subdirectories within the given top-level source directory that contain ANTLR
grammar files to generate output in a corresponding subdirectory of the destination directory, using the given config options."
  ([^File src-dir ^File dest-dir] (compile-antlr src-dir dest-dir nil))
  ([^File src-dir ^File dest-dir antlr-opts]
    (let [input-dirs (dirs-with-type src-dir file-types)]
      (if (empty? input-dirs)
        (println "ANTLR source directory" (.getPath src-dir) "is empty.")
        (let [output-dirs (absolute-files dest-dir (relative-paths src-dir input-dirs))]
          (doseq [[input-dir output-dir] (map list input-dirs output-dirs)]
            (process-antlr-dir input-dir output-dir antlr-opts)))))))

(defn antlr-src-dir "Determine the ANTLR source directory for the project."
  [project] (File. (get project :antlr-src-dir "src/antlr")))

(defn antlr-dest-dir "Determine the ANTLR target directory for the project."
  [project] (File. (get project :antlr-dest-dir "gen-src")))

(defn antlr-options "Determine the ANTLR config options for the project."
  [project] (get project :antlr-options))

(defn antlr
  "Generate Java source from an ANTLR grammar.

A typical project configuration will look like:

(defproject my-project
  ...
  :antlr-src-dir \"src/antlr\"
  :antlr-dest-dir \"gen-src\"
  :antlr-options { ... }
  ...
)

See https://github.com/alexhall/lein-antlr for a full listing of configuration options."
  [project]
  (compile-antlr (antlr-src-dir project)
                 (antlr-dest-dir project)
                 (antlr-options project)))

(defn clean-antlr-hook
  "Clean the ANTLR output directory."
  [f & [project & _ :as args]]
  (apply f args)
  (clean/delete-file-recursively (antlr-dest-dir project) true))

;; Add a hook to the "lein clean" task to clean the ANTLR target directory.
(defn activate []
  (add-hook #'clean/clean clean-antlr-hook))