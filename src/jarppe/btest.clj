(ns jarppe.btest
  (:require [jarppe.btest.core :as core]
            [jarppe.btest.commands :as c]
            [jarppe.btest.util :as u]
            [jarppe.btest.report :as r]))

(def clear! core/clear!)

(u/immigrate 'jarppe.btest.commands)

(defmacro deftest [test-name & body]
  `(do
     (r/test-start ~(name test-name))
     (try
       ~@body
       (r/test-success)
       (catch Exception e#
         (r/test-fail e#)))
     (r/test-end)))
