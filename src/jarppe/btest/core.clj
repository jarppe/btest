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
  (println "INCOMING:" response)
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

(defn submit [command-name args]
  (let [p (promise)]
    (.put command-queue {:promise  p
                         :name     command-name
                         :args     args
                         :created  (System/currentTimeMillis)})
    p))

(defmacro fail! [message & [response]]
  `(throw (clojure.lang.ExceptionInfo.
            (format "%s: [%s:%d] %s: %s" (apply str ~message) (or ~'file "NO_SOURCE_PATH") (or ~'line 0) ~'command-name (str ~response))
            {:type     :error
             :response ~response})))

(def ^:private timeout {:response {:status "timeout"}})

(defn execute [command-name args & [file line]]
  (let [p (submit command-name args)
        r (deref p 2000 timeout)
        s (get-in r [:response :status])]
    (condp = s
      "timeout" (fail! "timeout")
      "fail"    (fail! "fail" (get-in r [:response :result]))
      "ok"      r
      (fail! (str "unexpected status: '" s "'")))))
