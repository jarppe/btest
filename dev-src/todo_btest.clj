(ns todo-btest
  (:require [jarppe.btest :refer :all]
            [jarppe.btest.local-browser :as browser]))

;(browser/open-browser :firefox "http://localhost:8080/")

(defn login [username password]
  (set-value "#login-username" username)
  (set-value "#login-password" password)
  (click "#login button[type='submit']"))

(login "fofo" "baba")
(deftest cant-login-with-wrong-password
  (doseq [[username password] [["foo" "bar"] ["" "bar"] ["foo" ""]]]
    (login username password)
    (visible "#login .error-msg")))

(deftest successfull-login
  (login "foo" "foo")
  (invisible "#login .error-msg"))

(deftest logout
  (click "#todo a.logout")
  (visible "#login-username")
  (invisible "#login .error-msg"))
