(ns github-scoring-service.config
  (:require [environ.core :refer [env]]))

(def config 
  {:use-database (env :use-database)})
