(ns github-scoring-service.handler
  (:require [clojure.string :as string] 
            [compojure.core :refer :all]
            [compojure.route :as route]
            [github-scoring-service.event_processor :as event]
            [github-scoring-service.users :as users]
            [github-scoring-service.db :as db]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :as middleware]))

(defn get-user-score-handler
  [userID]
  (let [user-score (users/get-user-score userID)]
    {:status 200 :body {:user "Robert" :score user-score}}))

(defn process-event-handler
  [request]
  (if-let [process-status (event/process-event request)]
    {:status 200}
    {:status 500}))

(defn health-check
  "A health check endpoint that will also print the git hash"
  []
  {:status 200 :body (str "I'm Alive")})

(defroutes app-routes
  (GET "/score/user/:userID" [userID] (get-user-score-handler userID))
  (POST "/event" request (process-event-handler  request))
  (ANY "/health_check" [] (health-check))
  (route/not-found "Not Found"))

(def app-handler (-> app-routes
                     (middleware/wrap-json-response)
                     (middleware/wrap-json-body)
                     (wrap-defaults api-defaults)))
