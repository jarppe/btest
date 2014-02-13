(defproject jarppe.btest "0.0.5-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [slingshot "0.10.3"]
                 [ring "1.2.1"]
                 [ring/ring-json "0.2.0"]]
  :profiles {:dev {:dependencies [[compojure "1.1.6"]]
                   :jvm-opts ["-Xverify:none"]}})
