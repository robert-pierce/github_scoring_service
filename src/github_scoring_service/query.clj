(ns github-scoring-service.query
  (:require [clojure.string :as str]))

(defn get-users-query
  "Returns a mysql query to get a list of users. If passed a string
  parameter it will return a query that filters on repositories that 
  match that parameter."
  ([]
   (str "SELECT DISTINCT sender FROM events"))
  ([repository]
   (str "SELECT DISTINCT sender FROM events WHERE repository=\"" repository "\"")))

(defn get-repositories-query
  "Returns a mysql query to get a list of repositories. 
  If passed a string parameter it will return a query 
  that filters on users that match that parameter."
  ([]
   (str "SELECT DISTINCT repository FROM events"))
  ([user]
   (str "SELECT DISTINCT repository FROM events WHERE sender=\"" user "\"")))

(defn get-user-score-query
  "Returns a mysql query to get a user's score. If passed 
  a second string parameter it will return a query that 
  filters on repositories that match that parameter."
  ([user]
   (str "SELECT SUM(point_value) FROM events WHERE sender=\"" user "\""))
  ([user repository]
   (str "SELECT SUM(point_value) FROM events WHERE sender=\"" user "\" AND repository=\"" repository "\"")))

(defn get-user-history-query
  "Returns a mysql query to get a user's history. If passed 
  a second string parameter it will return a query that 
  filters on repositories that match that parameter."
  ([user]
   (str "SELECT type, point_value, repository, time_stamp FROM events WHERE sender=\"" user "\""))
  ([user repository]
   (str "SELECT type, point_value, repository, time_stamp FROM events WHERE sender=\"" user "\" AND repository=\"" repository "\"")))

(defn get-leaderboard-query
  "Returns a mysql query to get a list of users sorted from highest to lowest score. 
  If passed a string parameter it will return a query that filters on repositories that 
  match that parameter."
  ([]
   (str "SELECT sender as user, SUM(point_value) as points FROM events GROUP BY sender ORDER BY points DESC"))
  ([repository]
   (str "SELECT sender as user, SUM(point_value) as points FROM events WHERE repository=\"" repository "\" GROUP BY sender ORDER BY points DESC")))
