(ns todo
  (:require [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]
            [ring.middleware.json :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [jarppe.btest.core :as btest]))

(defn resource [resource-name content-type]
  (-> resource-name io/resource io/input-stream resp/response (resp/content-type content-type)))

(def app
  (-> (routes
        (GET  "/" [] (resource "sample/index.html" "text/html"))
        (GET  "/dev/btest.js" [] (resource "btest.js" "text/javascript"))
        (POST "/dev/btest" {resp :body} (btest/browser-command resp))
        (route/resources "/" {:root "sample"})
        (route/not-found "Not found"))
    (json/wrap-json-response)
    (json/wrap-json-body {:keywords? true})))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 8080 :join? false}))
