(ns github-scoring-service.db
  (:require [clojure.java.jdbc :as sql]
            [github-scoring-service.config :as c]))

(def db {:classname "com.mysql.jdbc.Driver"
         :subprotocol "mysql"
         :subname (format "//%s" (:db-url c/config))
         :user (:db-username c/config)
         :password (:db-password c/config)})

(defn show-tables 
  []
  (sql/query db
    ["show tables"]))
