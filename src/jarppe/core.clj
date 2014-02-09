(ns jarppe.btest.core
  (:import [java.util.concurrent LinkedBlockingDeque TimeUnit]))

(set! *warn-on-reflection* true)

(def ^LinkedBlockingDeque command-queue (LinkedBlockingDeque.))
(def current-command (atom nil))

(defn clear! []
  (.clear command-queue)
  (reset! current-command nil)
  nil)

(defn browser-command [response]
  (if response
    (if-let [{p :promise} @current-command]
      (deliver p response)
      (reset! current-command nil)))
  (if-let [command (.pollFirst command-queue 10000 TimeUnit/MILLISECONDS)]
    (do
      (reset! current-command (assoc command :started (System/currentTimeMillis)))
      {:status 200 :body {:status "command" :command (:name command) :args (:args command)}})
    {:status 200 :body {:status "timeout"}}))

(defn command [c & args]
  (let [p (promise)]
    (.put command-queue {:name c
                         :args args
                         :promise p
                         :created (System/currentTimeMillis)})
    p))
