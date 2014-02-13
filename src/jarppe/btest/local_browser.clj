(ns jarppe.btest.local-browser
  (:require [clojure.java.io :as io]
            [slingshot.slingshot :refer [throw+]]
            [jarppe.btest.core :as core]))

(def common-browsers
  {"Mac OS X" {:firefox  "/Applications/Firefox.app/Contents/MacOS/firefox"
               :safari   "/Applications/Safari.app/Contents/MacOS/Safari"
               :chromium "/Applications/Chromium.app/Contents/MacOS/Chromium"
               :chrome   "/Applications/Chrome.app/Contents/MacOS/Chrome"}
   "Linux"    {:firefox  "firefox"
               :chromium "chromium"
               :chrome   "chrome"}})

(def ^:private process (atom nil))

(defn close-browser []
  (when-let [p @process]
    (reset! process nil)
    (.destroy p)))

(defn open-browser [browser url]
  (close-browser)
  (let [app (if (keyword? browser)
              (get-in common-browsers [(System/getProperty "os.name") browser])
              browser)]
    (if-not app (throw (RuntimeException. (str "Unknown browser: " browser))))
    (reset! process (-> (ProcessBuilder. [app url]) (.start)))))

(defonce browser-config (atom nil))

(defn set-browser! [browser url]
  (reset! browser-config {:browser browser :url url}))

(defn require-browser []
  (when-not @process
    (let [{browser :browser url :url} @browser-config]
      (assert (and browser url) "Must set browser configuration: set-browser!")
      (core/clear!)
      (open-browser browser url)
      (let [p (core/submit {:name "ping" :args ["hello"] :file *file* :line 0})]
        (when (= (deref p 10000 :timeout) :timeout)
          (close-browser)
          (println "Browser timeout!")
          (throw+ {:core/source :require-browser})))
      (.addShutdownHook (Runtime/getRuntime) (Thread. close-browser))
      true)))
