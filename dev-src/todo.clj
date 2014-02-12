(ns todo
  (:require [clojure.java.io :as io]
            [clojure.walk :refer [keywordize-keys]]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [jarppe.btest.core :as btest]))

(defn resource [resource-name content-type]
  (-> resource-name io/resource io/input-stream resp/response (resp/content-type content-type)))

(defn wrap-json [handler]
  (fn [{:keys [content-type body] :as request}]
    (let [json-body (if (and content-type (.startsWith content-type "application/json") body)
                      (-> body
                        (io/reader :encoding (or (:character-encoding request) "utf-8"))
                        json/parse-stream
                        keywordize-keys))
          response (handler (assoc request :body json-body :json json-body))
          response-body (:body response)]
      (if (or (map? response-body) (sequential? response-body))
        (-> response
          (assoc :body (json/encode response-body))
          (resp/content-type "application/json; charset=utf-8")) 
        response))))

(def app
  (-> (routes
        (GET  "/" [] (resource "sample/index.html" "text/html"))
        (GET  "/dev/btest.js" [] (resource "js/btest.js" "text/javascript"))
        (POST "/dev/btest" {resp :body} (btest/browser-command resp))
        (route/resources "/" {:root "sample"})
        (route/not-found "Not found"))
    (wrap-json)))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 8080 :join? false}))
