(ns github-scoring-service.users
  (:require [clojure.string :as str]
            [clj-log.core :refer :all]
            [github-scoring-service.db :as db]))

(defn get-user-score
  [userID]
  42)

(defn get-users
  [repository]
  (try
    (if  (and (not (nil? repository)) (not (str/blank? repository)))
      (db/get-users repository)
      (db/get-users))
    (catch Exception e
      (log :warn (str "There was an exception trying to query the database. Exception " e)))))


(defn get-user-score
  [user repository]
  (try
    (if  (and (not (nil? repository)) (not (str/blank? repository)))
      (db/get-user-score user repository)
      (db/get-user-score user))
    (catch Exception e
      (log :warn (str "There was an exception trying to query the database. Exception " e)))))
