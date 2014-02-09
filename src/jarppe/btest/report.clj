(ns jarppe.btest.report)

(defn test-start [test-name]
  (println "Start:" test-name))

(defn test-step [step]
  (println "Testing:" step (eval step)))

(defn test-success []
  (println "Success"))

(defn test-fail [^Throwable e]
  (println "Failed:" (class e) (.getMessage e))
  (.printStackTrace e))

(defn test-end []
  (println "End"))
