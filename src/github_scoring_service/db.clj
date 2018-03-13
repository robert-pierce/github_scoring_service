(ns github-scoring-service.db
  (:require [clojure.java.jdbc :as sql]
            [github-scoring-service.config :as c]
            [github-scoring-service.query :as query]))

(defstruct events-row :id :type :point_value :sender :repository)

(def db
  "This map represents the database connection"
  {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname (format "//%s" (:db-url c/config))
         :user (:db-username c/config)
         :password (:db-password c/config)})

(defn show-tables
  "Queries the database to list the tables. 
  Used at startup for checking database connection"
  []
  (sql/query db
             ["show tables"]))

(defn persist-event!
  "Inserts a row in the events table"
  [id type point_value sender repository]
  (sql/insert! db
               :events
               (struct events-row id type point_value sender repository)))

(defn get-users
  "Queries the database to return a list of users.
  If passed a string parameter it will attempt to filter 
  on repositories that match that parameter"
  ([]
   (sql/query db [(query/get-users-query)]))
  ([repository]
   (sql/query db [(query/get-users-query repository)])))

(defn get-repositories
  "Queries the database to return a list of repositories.
  If passed a string parameter it will attempt to filter
  on users that match that parameter"
  ([]
   (sql/query db [(query/get-repositories-query)]))
  ([user]
   (sql/query db [(query/get-repositories-query user)])))

(defn get-user-score
  "Queries the database to return a user score. It should always
  recieve one string parameter that reporesents the user to look-up.
  If passed a second string parameter it will attempt to filter on
  repositories that match that parameter."
  ([user]
   (sql/query db [(query/get-user-score-query user)]))
  ([user repository]
   (sql/query db [(query/get-user-score-query user repository)])))

(defn get-user-history
  "Queries the database to return a user history. It should always
  recieve one string parameter that reporesents the user to look-up.
  If passed a second string parameter it will attempt to filter on
  repositories that match that parameter."
  ([user]
   (sql/query db [(query/get-user-history-query user)]))
  ([user repository]
   (sql/query db [(query/get-user-history-query user repository)])))

(defn get-leaderboard
  "Queries the database to return a list of users sorted
  in order from highest score to lowest score. If passed 
  a string parameter it will attempt to filter on 
  repositories that match that parameter"
  ([]
   (sql/query db [(query/get-leaderboard-query)]))
  ([repository]
   (sql/query db [(query/get-leaderboard-query repository)])))
