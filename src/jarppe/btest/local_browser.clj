(ns jarppe.btest.local-browser
  (:require [clojure.java.io :as io]))

(def common-browsers
  {"Mac OS X" {:firefox  "/Applications/Firefox.app/Contents/MacOS/firefox"
               :safari   "/Applications/Safari.app/Contents/MacOS/Safari"
               :chromium "/Applications/Chromium.app/Contents/MacOS/Chromium"}})

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
    (if-not (.exists (io/file app)) (throw (RuntimeException. (str "Browser '" app "' not found"))))
    (if-not (.canExecute (io/file app)) (throw (RuntimeException. (str "Browser '" app "' not executable"))))
    (reset! process (-> (ProcessBuilder. [app url]) (.start)))))
