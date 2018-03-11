(ns github-scoring-service.users
  (:require [clojure.string :as str]
            [clj-log.core :refer :all]
            [github-scoring-service.db :as db]))

(defn get-users
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
