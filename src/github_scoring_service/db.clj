(ns github-scoring-service.db
  (:require [clojure.java.jdbc :as sql]
            [github-scoring-service.config :as c]))

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
