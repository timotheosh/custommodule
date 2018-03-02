(ns custommodule.core
  (:require [com.stuartsierra.component :as component]
            [me.raynes.conch
             :refer [programs
                     with-programs
                     let-programs]
             :as sh]
            [clojure.string :as str])
  (:gen-class
   :name custommodule.core
   :methods [#^{:static true}
             [GET [String] java.util.HashMap]
             [POST [String] java.util.HashMap]
             [HEAD [String] java.util.HashMap]]))

(defrecord Command [cmd data]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (sh/programs bash)
    (let [data (bash "-c" (:cmd this))]
      (assoc this :data data)))

  (stop [this]
    this))

(defn execute-command [cmd]
  (let [command (map->Command {:cmd cmd})
        data (:data (.start command))]
    (.stop command)
    data))

(defn GET
  [cmd]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (execute-command cmd)})

(defn POST
  [cmd]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (execute-command cmd)})

(defn HEAD
  [cmd]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (execute-command cmd)})

(defn -main
  "Test Command component"
  [& args]
  (GET "for x in $(find /tmp/* -type d -prune);do du -sh $x; done"))
