(ns todo.test.login-test
  (:require [jarppe.btest :refer :all]
            [jarppe.btest.local-browser :as browser]))

(defn login [username password]
  (set-value "#login-username" username)
  (set-value "#login-password" password)
  (click "#login button[type='submit']"))

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
