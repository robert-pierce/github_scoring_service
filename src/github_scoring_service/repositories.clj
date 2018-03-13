(ns github-scoring-service.repositories
  (:require [clojure.string :as str]
            [clj-log.core :refer :all]
            [github-scoring-service.db :as db]))

(defn get-repositories
  "Queries the database to get a list of repositories. If passed a string
  parameter it will filter on the user that matches that parameter."
  ([]
   (try
     (let [get-value (comp val first)]
       (map get-value (db/get-repositories)))
     (catch Exception e
       (log :warn (str "There was an exception in get-repositories. Exception " e))
       (throw e))))
  ([user]
    (try
      (let [get-value (comp val first)]
        (map get-value (db/get-repositories user)))
     (catch Exception e
       (log :warn (str "There was an exception in get-repositories. Exception " e))
       (throw e)))))
