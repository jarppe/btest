(ns jarppe.btest
  (:require [slingshot.slingshot :refer [try+ throw+]]
            [jarppe.btest.core :as core]
            [jarppe.btest.local-browser :as browser]
            [jarppe.btest.report :as r]))

(def clear! core/clear!)

(def ^:private timeout {:response {:status "timeout"}})

(defn execute [command]
  (r/command-start command)
  (let [p (core/submit command)
        r (deref p 4000 timeout)
        s (get-in r [:response :status])]
    (condp = s
      "ok"      (r/command-success command r)
      "fail"    (r/command-fail command r)
      "timeout" (r/command-error command :timeout)
      (throw (RuntimeException.
               (format "Unexpected status: '%s', command: %s %s (%s:%d)" s (:name command) (:args command) (:file command) (:line command)))))))

(defmacro defcommand [command-name args]
  (let [args (vec args)]
    (list 'clojure.core/defmacro command-name args
      (list 'list `execute {:name (str command-name) :args args :file '*file* :line '(:line (meta &form))}))))

(defmacro deftest [test-name & body]
  `(try+
     (r/test-start ~(name test-name))
     (do ~@body)
     (r/test-success)
     (catch (= :fail ~'%) e#
       (r/test-fail e#))
     (catch (= :abort ~'%) e#
       (throw+))))

(defn run-tests [namespaces & opts]
  (let [opts (apply hash-map opts)]
    (try+
      (browser/require-browser)
      (r/run-test-start opts)
      (doseq [n namespaces]
        (r/run-test-namespace (name n))
        (require (symbol n) (if (:reload-all opts) :reload-all :reload)))
      (catch (= % :abort) _
        (r/aborted)))
    (r/run-test-done)))

(defcommand exists     [selector])
(defcommand visible    [selector])
(defcommand invisible  [selector])
(defcommand enabled    [selector])
(defcommand disabled   [selector])
(defcommand click      [selector])
(defcommand url-hash   [expected-hash])
(defcommand value      [selector expected-value])
(defcommand text       [selector expected-text])

(defcommand get-hash [])
(defcommand set-hash [new-hash])
(defcommand get-value [selector])
(defcommand set-value [selector new-value])

(defcommand ping [message])
(defcommand load-app [url])
(defcommand reload [])
