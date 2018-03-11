(ns github-scoring-service.handler
  (:require [clojure.string :as string] 
            [compojure.core :refer :all]
            [compojure.route :as route]
            [github-scoring-service.event_processor :as event]
            [github-scoring-service.users :as users]
            [github-scoring-service.db :as db]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :as middleware]))

(defn get-users-handler
  [repository]
  (if-let [users (users/get-users repository)]
    {:status 200 :body users}
    {:status 500 :body (str "There was a server error")}))

(defn get-user-score-handler
  [user repository]
  (if-let [users (users/get-user-score user repository)]
    {:status 200 :body users}
    {:status 500 :body (str "There was a server error")}))


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
  (GET "/api/users" [repository] (get-users-handler repository))
  (GET "/api/users/:user/score" [user repository] (get-user-score-handler user repository))
  (POST "/event" request (process-event-handler  request))
  (ANY "/health_check" [] (health-check))
  (route/not-found "Not Found"))

(def app-handler (-> app-routes
                     (middleware/wrap-json-response)
                     (middleware/wrap-json-body)
                     (wrap-defaults api-defaults)))
