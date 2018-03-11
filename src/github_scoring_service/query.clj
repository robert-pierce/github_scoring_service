(ns github-scoring-service.query
  (:require [clojure.string :as str]))


(defn get-users-query
  ([]
   (str "SELECT DISTINCT sender FROM events"))
  ([repository]
   (str "SELECT DISTINCT sender FROM events WHERE repository=\"" repository "\"")))

(defn get-user-score-query
  ([user]
   (str "SELECT SUM(point_value) FROM events WHERE sender=\"" user "\""))
  ([user repository]
   (str "SELECT SUM(point_value) FROM events WHERE sender=\"" user "\" AND repository=\"" repository "\"")))
