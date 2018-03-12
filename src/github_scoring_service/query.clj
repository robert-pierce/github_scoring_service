(ns github-scoring-service.query
  (:require [clojure.string :as str]))


(defn get-users-query
  ([]
   (str "SELECT DISTINCT sender FROM events"))
  ([repository]
   (str "SELECT DISTINCT sender FROM events WHERE repository=\"" repository "\"")))

(defn get-repositories-query
  ([]
   (str "SELECT DISTINCT repository FROM events"))
  ([user]
   (str "SELECT DISTINCT repository FROM events WHERE sender=\"" user "\"")))

(defn get-user-score-query
  ([user]
   (str "SELECT SUM(point_value) FROM events WHERE sender=\"" user "\""))
  ([user repository]
   (str "SELECT SUM(point_value) FROM events WHERE sender=\"" user "\" AND repository=\"" repository "\"")))

(defn get-user-history-query
  ([user]
   (str "SELECT type, point_value, repository, time_stamp FROM events WHERE sender=\"" user "\""))
  ([user repository]
   (str "SELECT type, point_value, repository, time_stamp FROM events WHERE sender=\"" user "\" AND repository=\"" repository "\"")))

(defn get-leaderboard-query
  ([]
   (str "SELECT sender as user, SUM(point_value) as points FROM events GROUP BY sender ORDER BY points DESC"))
  ([repository]
   (str "SELECT sender as user, SUM(point_value) as points FROM events WHERE repository=\"" repository "\" GROUP BY sender ORDER BY points DESC")))
