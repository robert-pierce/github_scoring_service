(ns github-scoring-service.init
  (:require [clj-log.core :refer :all]
            [github-scoring-service.db :as db]
            [github-scoring-service.config :as c]))

(defn dev-env-info
  []
  (log :info (str "Database Username: " (:db-username c/config)))
  (log :info (str "Database Url: " (:db-url c/config))))

(defn check-db
  []
  (try 
    (let [test-query-result (db/show-tables)]
      (if (> (count test-query-result) 0)
        (log :info (str "The app is connected to a database with at least one table"))
        (if (= (count test-query-result) 0)
          (log :warn (str "The app seems to be connected to a database, but there are no tables present"))
          (log :warn (str "The app does not appear to be connected to a database")))))
   (catch Exception e
     (log :warn (str "Not able to connect to database. Exception:  " (.getMessage e))))))

(defn init-app
  "Any functions that you want to be called at app start up can go here. 
  Useful for checking the state of the app at startup (i.e. database connection etc)"
  []
  (log :info "Initializing Github_Scoring_Service")
  (dev-env-info)
  (check-db))
