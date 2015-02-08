lein-antlr
==========

**lein-antlr4** is a [Leiningen 2](https://github.com/technomancy/leiningen) plugin for generating source
code from one or more [ANTLR4](http://www.antlr.org) grammars in a Leiningen project. It is derived from the [lein-antlr](https://github.com/alexhall/lein-antlr) for version 3 of ANTLR.

It has roughly
the same functionality as the Maven ANTLR plugin, and is intended to allow developers to integrate
ANTLR-generated source code into a Clojure project without resorting to Maven or some other manual process.

To use <tt>lein-antlr4</tt> in your project, simply add it to <tt>:plugins</tt> in your <tt>project.clj</tt>:

    :plugins [[lein-antlr4 "0.1.0-SNAPSHOT"]]

*Warning*: This is **incomplete**. For basic use cases it works, but it may not properly support many of the command line options of ANTLR tool and there is is currently no work being done to test or support other features. 
	
Usage
-----

The lein-antlr plugin can be called from the command-line as follows:

    % lein antlr4

The plugin is configured in your <tt>project.clj</tt> as follows:

    (defproject my-project
      ...
      :antlr-src-dir "src/antlr"
      :antlr-dest-dir "gen-src"
      :antlr-options {:verbose true
                      ... }
    )

The plugin will scan the source directory specified by <tt>:antlr-src-dir</tt> and its subdirectories for all
ANTLR grammar files (i.e. those files whose names end in '.g4' or '.g') and compile them, placing the generated
source code into the destination directory specified by <tt>:antlr-dest-dir</tt>. Grammar files located in
subdirectories of the source directory will have their generated code placed into corresponding subdirectories
in the destination directory.

The default values for <tt>:antlr-src-dir</tt> and <tt>:antlr-dest-dir</tt> are 'src/antlr' and 'gen-src' respectively.

Options
-------

The behavior of the ANTLR tool is configured using the <tt>:antlr-options</tt> entry in your project
description. This entry should be a map of keyword-value pairs as follows:

<table border="1" cellspacing="3" cellpadding="5">
 <tr>
  <th>Option</th>
  <th>Type</th>
  <th>Default Value</th>
  <th>Description</th>
 </tr>
 <tr>
  <td><tt>:atn</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td> Generate rule augmented transition network diagrams.</td>
 </tr>
 <tr>
  <td><tt>:message-format</tt></td>
  <td>String</td>
  <td>"antlr"</td>
  <td>Determines the format to use for warning and error messages returned by ANTLR.
Should be one of "antlr", "gnu", or "vs2005".</td>
 </tr>
 <tr>
  <td><tt>:listener</tt></td>
  <td>boolean</td>
  <td>true</td>
  <td> Whether to generate a parse tree listener.</td>
 </tr>
 <tr>
  <td><tt>:visitor</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td> Whether to generate a parse tree visitor.</td>
 </tr>
 <tr>
  <td><tt>:package</tt></td>
  <td>String</td>
  <td>false</td>
  <td>Specify a package/namespace for the generated code.</td>
 </tr>
 <tr>
  <td><tt>:encoding</tt></td>
  <td>String</td>
  <td>"UTF-8"</td>
  <td>Specify the encoding of the grammar files</td>
 </tr>
 <tr>
  <td><tt>:depend</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td>Generate file dependencies.</td>
 </tr>
 <tr>
  <td><tt>:D</tt></td>
  <td>map</td>
  <td>{}</td>
  <td>Set/override grammar-level options to values specified by the map.</td>
 </tr>
 <tr>
  <td><tt>:warn-error</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td>Treat warnings as errors.</td>
 </tr>
 <tr>
  <td><tt>:save-lexer</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td>Save temporary lexer file created for combined grammars.</td>
 </tr>
 <tr>
  <td><tt>:debug-string-template</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td>Launch StringTemplate visualizer on generated code.</td>
 </tr>
 <tr>
  <td><tt>:force-atn</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td>Use the ATN simulator for all predictions.</td>
 </tr>
 <tr>
  <td><tt>:log</tt></td>
  <td>boolean</td>
  <td>false</td>
  <td>Dump logging info to antlr-timestamp.log.</td>
 </tr>
</table>

Cleaning Up
-----------

The plugin can be configured to clean the generated source directory as part of the Leiningen 'clean'
task, but this must be manually set up by adding the <tt>leiningen.antlr4</tt> namespace to the project
hooks, like so:

    (defproject my-project
      ...
      :hooks [leiningen.antlr4]
      ...
    )

---

License & Copyright
-------------------

This is a derived work of the lein-antlr project.

Copyright (c) 2010 Revelytix, Inc.

The lein-antlr project is distrubuted under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

There have been modifications to this project to form the lein-antlr4 project.

All modifications are Copyright (c) 2013 Edward Ross
and distributed under [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
