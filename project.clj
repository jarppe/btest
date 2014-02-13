(defproject jarppe.btest "0.0.4"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [slingshot "0.10.3"]]
  :profiles {:dev {:dependencies [[ring "1.2.1"]
                                  [cheshire "5.3.0"]
                                  [compojure "1.1.6"]]
                   :jvm-opts ["-Xverify:none"]}})
