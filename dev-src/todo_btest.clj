(ns todo-btest
  (:require [jarppe.btest :refer :all]
            [jarppe.btest.local-browser :as browser]))

;(browser/open-browser :firefox "http://localhost:8080/")

(set-value "#login-username" "foo")
(set-value "#login-password" "bar")
(invisible "#login .error-msg")
(click "#login button[type='submit']")
(visible "#login .error-msg")
(set-value "#login-password" "foo")
(click "#login button[type='submit']")
(invisible "#login .error-msg")
(click "#todo a.logout")
(visible "#login-username")
