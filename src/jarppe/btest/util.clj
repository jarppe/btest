(ns jarppe.btest.util)

;;
;; copied from Midje midje.clojure.core
;;

(defn var-root [var]
  (alter-var-root var identity))

(letfn [(move-var [var sym]
          (let [sym (with-meta sym (assoc (meta var) :ns *ns*))]
            (if (.hasRoot var)
              (intern *ns* sym (var-root var))
              (intern *ns* sym))))]
  
  (defn immigrate
    "Create a public var in this namespace for each public var in the
     namespaces named by ns-names. The created vars have the same name, root
     binding, and metadata as the original except that their :ns metadata
     value is this namespace."
    [& ns-names]
    (doseq [ns ns-names]
      (require ns)
      (doseq [[sym ^clojure.lang.Var var] (ns-publics ns)]
        (move-var var sym))))
  
  (defn immigrate-from
    "Like 'immigrate', except wth a list of named symbols."
    [ns symbols]
    (doseq [sym symbols]
      (move-var (ns-resolve ns sym) sym))))
