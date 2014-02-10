(ns jarppe.btest
  (:require [slingshot.slingshot :refer [try+]]
            [jarppe.btest.core :as core]
            [jarppe.btest.commands :as c]
            [jarppe.btest.util :as u]
            [jarppe.btest.report :as r]))

(def clear! core/clear!)

(u/immigrate 'jarppe.btest.commands)

(defmacro deftest [test-name & body]
  `(do
     (r/test-start ~(name test-name))
     (try+
       (do ~@body)
       (r/test-success)
       (catch [:jarppe.btest.core/source :jarppe.btest.core/btest] e#
         (r/test-fail e#))
       (catch Object e#
         (r/test-error e# (:throwable ~'&throw-context))))
     (r/test-end)))
