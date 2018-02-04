(ns custommodule.core
  (:require [com.stuartsierra.component :as component]
            [me.raynes.conch
             :refer [programs
                     with-programs
                     let-programs]
             :as sh]
            [clojure.string :as str]))

(defrecord Command [cmd data]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [this]
    (sh/programs bash)
    (let [data (bash "-c" (:cmd this))]
      (assoc this :data data)))

  (stop [this]
    this))

(defn new-command [cmd]
  (map->Command {:cmd cmd}))

(defn get-data [command]
  (:data (.start command)))

(defn -main
  "Test Command component"
  [& args]
  (let [data
        (get-data
         (new-command
          "for x in $(find /tmp/* -type d -prune);do du -sh $x; done"))]
    (println data)))
