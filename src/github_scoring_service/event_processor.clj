(ns github-scoring-service.event_processor
  (:require [clojure.string :as str]
            [clj-log.core :refer :all]
            [github-scoring-service.config :as c]
            [github-scoring-service.db :as db]))

(def value-map {"push" (Integer/parseInt (:push-event-value c/config)) 
                "pull_request_review_comment" (Integer/parseInt (:pr-comment-value c/config)) 
                "watch" (Integer/parseInt (:watch-event-value c/config)) 
                "create" (Integer/parseInt (:create-event-value c/config))})

(defn get-event-point-value
  [event-type]
  (if-let [value (get value-map event-type)]
    value
    1))

(defn process-event
  [request]
  (let [headers (:headers request)
        body (:body request)
        event-type (get headers "x-github-event")
        guid (get headers "x-github-delivery")
        github-user (get-in body ["sender" "login"])
        repository (get-in body ["repository" "full_name"])
        event-value (get-event-point-value event-type)]
    (try 
      (db/persist-event! guid event-type event-value github-user repository)
      true
      (catch Exception e
        (log :error (str "Error saving event in the database. Exception: " (.getMessage e)))
        false))))
