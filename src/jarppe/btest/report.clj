(ns jarppe.btest.report
  (:require [clojure.string :as s]
            [slingshot.slingshot :refer [throw+]]
            [jarppe.btest.core :as core]))

(def ^:private initial-report {:success  0
                               :fail     0
                               :start    0
                               :opts     {}})

(def ^:private report (atom initial-report))

(defn esc [s] (str "\u001B[" s))

(def color (reduce-kv (fn [c k v] (assoc c k (esc v))) {}
             {:reset     "0m"
              :black     "30m"
              :red       "31m"
              :green     "32m"
              :yellow    "33m"
              :blue      "34m"
              :magenta   "35m"
              :cyan      "36m"
              :white     "37m"}))

(defn ansi [& args]
  (if-not (get-in @report [:opts :color])
    (s/join (->> args (remove keyword?) (map str)))
    (s/join (->> (conj args :reset) (map (fn [arg] (if (keyword? arg) (color arg "") (str arg))))))))

(defn run-test-start [opts]
  (reset! report (assoc initial-report
                   :start  (System/currentTimeMillis)
                   :opts   opts)))

(defn run-test-namespace [namespace-name]
  (println (ansi "Suite " :bold-on namespace-name :bold-off ":")))

(defn test-start [test-name]
  (print (ansi "  " test-name ": ")))

(defn command-start [command])

(defn command-success [command result])

(def default-message {"exists"     "Selected element does not exists"
                      "visible"    "Selected element should be visible"
                      "invisible"  "Selected element should not be visible"
                      "enabled"    "Selected element should be enabled"
                      "disabled"   "Selected element should be disabled"
                      "click"      "Can't click, element does not exist or is not enabled"
                      "url-hash"   "Page URL hash does not match"
                      "value"      "Selected element value dows not match"})

(defn command-fail [{:keys [name args file line]} result]
  (println (ansi :red "FAIL" :reset "\n    " name " " args " (" :white file :reset ":" :white line :reset "): " (or (-> result :command :response :message) (default-message name ""))))
  (swap! report update-in [:fail] inc)
  (throw+ (if (:continue-on-fail @report) :fail :abort)))

(defn command-error [{:keys [name args file line]} reason]
  (println (ansi :red "ERROR" :reset "\n    " :white (-> reason clojure.core/name s/upper-case) :reset " " name " " args " (" :white file :reset ":" :white line :reset ")"))
  (throw+ :abort))

(defn test-success []
  (println (ansi :green "OK"))
  (swap! report update-in [:success] inc)
  true)

(defn test-fail [result]
  false)

(defn aborted []
  (swap! report assoc :aborted true))

(defn run-test-done []
  (let [r        @report
        aborted  (:aborted r)
        success  (:success r)
        fail     (:fail r)
        duration (- (System/currentTimeMillis) (:start r))]
    (if aborted
      (println (ansi "\n" :red "ABORTED!"))
      (if (zero? fail)
        (println (ansi "\n" :green "Joy and rejoice, all tests passed!"))
        (println (ansi "\n" :yellow "To err is human - and to blame it on a computer is even more so. Now go fix your blunder!"))))
    (println (ansi "Success: " :green success :reset " Fail: " :red fail :reset " Time: " :cyan (format "%.3f" (/ duration 1000.0))))
    (and (not aborted) (zero? fail))))
