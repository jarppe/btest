(ns todo.todo-app
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]
            [ring.middleware.json :as json]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [jarppe.btest.core :as btest]))

(def app
  (-> (routes
        (GET  "/" [] (resp/redirect "/index.html"))
        (GET  "/btest/:res" [res] (btest/resource res))
        (POST "/btest" {resp :body} (btest/browser-command resp))
        (route/resources "/" {:root "sample"})
        (route/not-found "Not found"))
    (json/wrap-json-body {:keywords? true})
    (json/wrap-json-response)))

(defn -main [& args]
  (jetty/run-jetty (var app) {:port 3000 :join? false}))
