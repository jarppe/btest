(ns user
  (:require [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [jarppe.btest :as btest]
            [jarppe.btest.local-browser :as browser]
            [todo.todo-app :refer [-main]]))

(browser/set-browser! :firefox "http://localhost:3000/")

(defn run-tests [& opts]
  (apply btest/run-tests ["todo.test.login-test"] opts))
