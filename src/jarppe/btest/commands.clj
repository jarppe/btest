(ns jarppe.btest.commands
  (:refer-clojure :exclude [hash])
  (:require [jarppe.btest.core :as core]))

(defmacro ^:private defcommand [command-name & [args]]
  `(defmacro ~command-name [~@args]
     (core/execute
       ~(name command-name)
       ~args
       (clojure.core/unquote (quote *file*))
       (clojure.core/unquote (quote (:line (meta &form)))))))

(defcommand exists     [selector])
(defcommand visible    [selector])
(defcommand invisible  [selector])
(defcommand enabled    [selector])
(defcommand disabled   [selector])
(defcommand click      [selector])
(defcommand url-hash   [expected-hash])
(defcommand value      [selector expected-value])

(defcommand get-hash [])
(defcommand set-hash [hash])
(defcommand get-value [selector])
(defcommand set-value [selector new-value])

(defcommand ping [message])
