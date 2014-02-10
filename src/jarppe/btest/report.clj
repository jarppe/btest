(ns jarppe.btest.report)

(defn test-start [test-name]
  (print test-name ": "))

(defn test-success []
  (println "Success"))

(defn test-fail [result]
  (println "Fail:" result))

(defn test-error [^Throwable e]
  (println "Error:" (class e) (.getMessage e))
  (.printStackTrace e))

(defn test-end [])
