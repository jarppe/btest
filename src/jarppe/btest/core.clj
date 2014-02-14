(ns jarppe.btest.core
  (:require [clojure.java.io :as io]
            [slingshot.slingshot :refer [throw+]])
  (:import [java.util.concurrent LinkedBlockingDeque TimeUnit]))

(set! *warn-on-reflection* true)

(def ^LinkedBlockingDeque command-queue (LinkedBlockingDeque.))
(def current-command (atom nil))

(defn clear! []
  (.clear command-queue)
  (reset! current-command nil)
  nil)

(defn browser-command [response]
  (when-not (empty? response)
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

(def resource
  (let [content-types {".html" "text/html"
                       ".js"   "text/javascript"
                       ".css"  "text/css"}]
    (fn [resource-name]
      (let [content-type (content-types (re-find #"\.\w+$" resource-name) "text/plain")
            res (io/resource (str "btest/" resource-name))]
        (if res
          {:status  200
           :body    (io/input-stream res)
           :headers {"Content-Type" content-type}}
          {:status 404
           :body "not found"})))))
