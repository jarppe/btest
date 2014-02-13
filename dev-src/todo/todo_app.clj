(ns todo.todo-app
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
          request   (assoc request :json json-body)
          request   (if json-body (assoc request :body json-body) request)
          response  (handler request)
          body      (:body response)]
      (if (or (map? body) (sequential? body))
        (-> response
          (assoc :body (json/encode body))
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
  (jetty/run-jetty (var app) {:port 3000 :join? false}))
