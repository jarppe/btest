(ns jarppe.btest.commands
  (:refer-clojure :exclude [hash])
  (:require [jarppe.btest.core :as core]))

(def clear! core/clear!)

(defn command [file line command-name & args]
  (println (format "command: [%s/%d] '%s': %s" file line command-name args)))

(defmacro defcommand [command-name & [args]]
  `(defmacro ~command-name [~@args]
     (command (clojure.core/unquote (quote *file*)) (clojure.core/unquote (quote (:line (meta &form)))) ~(name command-name) ~@args)))

(defcommand hash [h x])

(hash "foo" "bar")
(hash "dozo" "dada")

;(defn hash [h]            (command "hash" h))
;(defn exists [selector]   (command "exists" selector))
;(defn visible [selector]  (command "visible" selector))
;(defn enabled [selector]  (command "enabled" selector))
;(defn disabled [selector] (command "disabled" selector))
;(defn click [selector]    (command "click" selector))
;(defn value [selector v]  (command "value" selector v))
;
;(defn get-hash []                (command "getHash"))
;(defn set-hash [h]               (command "setHash" h))
;(defn get-value [selector]       (command "getValue" selector))
;(defn set-value [selector value] (command "setValue" selector value))
;
;(defn ping [& args] (apply command "ping" args))
