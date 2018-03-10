(ns github-scoring-service.init
  (:require [clojure.string :as string]
            [clj-log.core :refer :all]
            [github-scoring-service.config :as c]))

(defn init-app
  []
  (log :info "Initializing Github_Scoring_Service")
  (log :info (str "Database Username: " (:db-username c/config)))
  (log :info (str "Database Url: " (:db-url c/config))))
