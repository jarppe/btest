(defproject jarppe.btest "0.0.2-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[ring "1.2.1"]
                                  [cheshire "5.3.0"]
                                  [compojure "1.1.6"]]
                   :jvm-opts ["-Xverify:none"]}})
