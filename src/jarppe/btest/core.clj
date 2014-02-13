(ns jarppe.btest.core
  (:require [slingshot.slingshot :refer [throw+]])
  (:import [java.util.concurrent LinkedBlockingDeque TimeUnit]))

(set! *warn-on-reflection* true)

(def ^LinkedBlockingDeque command-queue (LinkedBlockingDeque.))
(def current-command (atom nil))

(defn clear! []
  (.clear command-queue)
  (reset! current-command nil)
  nil)

(defn browser-command [response]
  (when response
    (when-let [c @current-command]
      (deliver (:promise c)
        (-> c
          (dissoc :promise)
          (assoc :response response
                 :done (System/currentTimeMillis))))
      (reset! current-command nil)))
  (if-let [command (.pollFirst command-queue 10000 TimeUnit/MILLISECONDS)]
    (do
      (reset! current-command (assoc command :started (System/currentTimeMillis)))
      {:status 200 :body {:status "command" :command (:name command) :args (:args command)}})
    {:status 200 :body {:status "timeout"}}))

(defn submit [{command-name :name args :args}]
  (let [p (promise)]
    (.put command-queue {:promise  p
                         :name     command-name
                         :args     args})
    p))
