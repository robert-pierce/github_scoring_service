(ns github-scoring-service.users
  (:require [clojure.string :as str]
            [clj-log.core :refer :all]
            [github-scoring-service.db :as db]))

(defn get-users
  "Queries the database to get a list of users. If passed a string
  parameter it will filter on the repository that matches that parameter."
  ([]
   (try
     (let [get-value (comp val first)]
       (map get-value (db/get-users)))
     (catch Exception e
       (log :warn (str "There was an exception in get-users. Exception " e))
       (throw e))))
  ([repository]
    (try
      (let [get-value (comp val first)]
        (map get-value (db/get-users repository)))
     (catch Exception e
       (log :warn (str "There was an exception in get-users. Exception " e))
       (throw e)))))

(defn get-user-score
  "Queries the database to get a user's score. If passed a second string
  parameter it will filter on the repository that matches that parameter."
  ([user]
   (try
     (let [get-value (comp val first first)] 
       (get-value (db/get-user-score user)))
     (catch Exception e
       (log :warn (str "There was an exception in get-user-score. Exception " e))
       (throw e))))
  ([user repository]
   (try
     (let [get-value (comp val first first)]
       (get-value (db/get-user-score user repository)))
     (catch Exception e
       (log :warn (str "There was an exception in get-user-score. Exception " e))
       (throw e)))))

(defn get-user-history
  "Queries the database to get a user's history. If passed a second string
  parameter it will filter on the repository that matches that parameter."
  ([user]
   (try
     (seq (db/get-user-history user))
     (catch Exception e
       (log :warn (str "There was an exception in get-user-history. Exception " e))
       (throw e))))
  ([user repository]
   (try
     (seq (db/get-user-history user repository))
     (catch Exception e
       (log :warn (str "There was an exception in get-user-history. Exception " e))
       (throw e)))))
