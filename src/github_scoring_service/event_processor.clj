(ns github-scoring-service.event_processor
  (:require [clojure.string :as str]))

(defn process-event
  [body]
  (str "the event has been processed"))
