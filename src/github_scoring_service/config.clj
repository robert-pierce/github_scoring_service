(ns github-scoring-service.config
  (:require [environ.core :refer [env]]))

(def config 
  {:db-username (env :db-username)
   :db-password (env :db-password)
   :db-url (env :db-url)})
