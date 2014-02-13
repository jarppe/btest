(ns todo.test.add-todos-test
  (:require [jarppe.btest :refer :all]
            [todo.test.login-test :refer [login]]))

(deftest add-todo
   (reload)
   (login "foo" "foo")
   (set-value "#new-todo-text" "hello")
   (click "#todo button[type='submit']")
   (text "#todo li" "hello"))