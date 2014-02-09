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

(defn submit [command-name args]
  (let [p (promise)]
    (.put command-queue {:promise  p
                         :name     command-name
                         :args     args
                         :created  (System/currentTimeMillis)})
    p))

(defn execute [command-name args & [file line]]
  (let [p (submit command-name args)
        r (deref p 2000 ::timeout)]
    (if (= r ::timeout)
      (throw (RuntimeException. (format "timeout: [%s:%d] %s" (or file "NO_SOURCE_PATH") (or line 0) command-name)))
      r)))
