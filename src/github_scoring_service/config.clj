(ns github-scoring-service.config
  (:require [environ.core :refer [env]]))

(def config
  "This map represents the values needed by the service
  that are loaded from the local environment"
  {:db-username (env :db-username)
   :db-password (env :db-password)
   :db-url (env :db-url)
   :push-event-value (env :push-event-value)
   :pr-comment-value (env :pr-comment-value)
   :watch-event-value (env :watch-event-value)
   :create-event-value (env :create-event-value)})
