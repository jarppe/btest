(ns jarppe.btest
  (:require [jarppe.btest.core :as core]
            [jarppe.btest.commands :as c]
            [jarppe.btest.util :as u]))

(def clear! core/clear!)

(u/immigrate 'jarppe.btest.commands)
