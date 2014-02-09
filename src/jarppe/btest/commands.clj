(ns jarppe.btest.commands
  (:refer-clojure :exclude [hash])
  (:require [jarppe.btest.core :as core]))

(defmacro ^:private defcommand [command-name args]
  (let [args (vec args)]
    (list 'clojure.core/defmacro command-name args
      (list 'list 'jarppe.btest.core/execute (str command-name) args '*file* '(:line (meta &form))))))

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
