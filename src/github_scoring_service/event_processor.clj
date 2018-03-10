(ns github-scoring-service.event_processor
  (:require [clojure.string :as str]))

(defn process-event
  [request]
  (println request)
  (:body request))
