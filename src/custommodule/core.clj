(ns custommodule.core
  (:require [com.stuartsierra.component :as component]
            [me.raynes.conch
             :refer [programs
                     with-programs
                     let-programs]
             :as sh]
            [clojure.string :as str])
  (:gen-class))

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

(defn -get
  [cmd]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (execute-command cmd)})

(defn -main
  "Test Command component"
  [& args]
  (-get "for x in $(find /tmp/* -type d -prune);do du -sh $x; done"))
