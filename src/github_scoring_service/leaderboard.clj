(ns github-scoring-service.leaderboard
  (:require [clojure.string :as str]
            [clj-log.core :refer :all]
            [github-scoring-service.db :as db]))

(defn get-leaderboard
  ([]
   (try
     (seq (db/get-leaderboard))
     (catch Exception e
       (log :warn (str "There was an exception in get-leaderboard. Exception " e))
       (throw e))))
  ([repository]
    (try
      (seq (db/get-leaderboard repository))
     (catch Exception e
       (log :warn (str "There was an exception in get-leaderboard. Exception " e))
       (throw e)))))
