(ns github-scoring-service.db
  (:require [clojure.java.jdbc :as sql]
            [github-scoring-service.config :as c]
            [github-scoring-service.query :as query]))

(defstruct events-row :id :type :point_value :sender :repository)

(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname (format "//%s" (:db-url c/config))
         :user (:db-username c/config)
         :password (:db-password c/config)})

(defn show-tables 
  []
  (sql/query db
             ["show tables"]))

(defn persist-event!
  [id type point_value sender repository]
  (sql/insert! db
               :events
               (struct events-row id type point_value sender repository)))

(defn get-users
  ([]
   (sql/query db [(query/get-users-query)]))
  ([repository]
   (sql/query db [(query/get-users-query repository)])))

(defn get-repositories
  ([]
   (sql/query db [(query/get-repositories-query)]))
  ([user]
   (sql/query db [(query/get-repositories-query user)])))

(defn get-user-score
  ([user]
   (sql/query db [(query/get-user-score-query user)]))
  ([user repository]
   (sql/query db [(query/get-user-score-query user repository)])))

(defn get-user-history
  ([user]
   (sql/query db [(query/get-user-history-query user)]))
  ([user repository]
   (sql/query db [(query/get-user-history-query user repository)])))

(defn get-leaderboard
  ([]
   (sql/query db [(query/get-leaderboard-query)]))
  ([repository]
   (sql/query db [(query/get-leaderboard-query repository)])))
