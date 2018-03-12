(ns github-scoring-service.handler
  (:require [clojure.string :as str] 
            [compojure.core :refer :all]
            [compojure.route :as route]
            [github-scoring-service.event_processor :as event]
            [github-scoring-service.users :as users]
            [github-scoring-service.repositories :as repos]
            [github-scoring-service.db :as db]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :as middleware]))

(defn get-users-handler
  [repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (if-let [users (users/get-users repository)]
      {:status 200 :body {:repository repository :users users}}
      {:status 200 :body {:message "No Results"}})
    (if-let [users (users/get-users)]
      {:status 200 :body {:users users}}
      {:status 200 :body {:message "No Results"}})))

(defn get-repositories-handler
  [user]
  (if (and (not (nil? user)) (not (str/blank? user)))
    (if-let [repos (repos/get-repositories user)]
      {:status 200 :body {:user user :repositories repos}}
      {:status 200 :body {:message "No Results"}})
    (if-let [repos (repos/get-repositories)]
      {:status 200 :body {:repositories repos}}
      {:status 200 :body {:message "No Results"}})))

(defn get-user-score-handler
  [user repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (try
      (if-let [score (users/get-user-score user repository)]
        {:status 200 :body {:user user :repository repository :score score}}
        {:status 200 :body {:user user :repository repository :message "No Results For This User and Repository"}})
      (catch Exception e
        {:status 500 :body (str "There was a server error")}))
    (try
      (if-let [score (users/get-user-score user)]
        {:status 200 :body {:user user :score score}}
        {:status 200 :body {:user user :message "No Results For This User"}})
      (catch Exception e
        {:status 500 :body (str "There was a server error")}))))

(defn get-user-history-handler
  [user repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (try
      (if-let [history (users/get-user-history user repository)]
        (if-let [score (users/get-user-score user repository)]
          {:status 200 :body {:user user :repository repository :score score :history history}}
          {:status 200 :body {:user user :history history :message "Could Not Get User Score"}})
        {:status 200 :body {:user user :repository repository :message "No History For This User and Repository"}})
      (catch Exception e
        {:status 500 :body (str "There was a server error")}))
    (try
      (if-let [history (users/get-user-history user)]
        (if-let [score (users/get-user-score user)] 
          {:status 200 :body {:user user :score score :history history}}
          {:status 200 :body {:user user :history history :message "Could Not Get User Score"}})
        {:status 200 :body {:user user :message "No History For This User"}})
      (catch Exception e
        {:status 500 :body (str "There was a server error")}))))



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
  (GET "/api/users/:user/history" [user repository] (get-user-history-handler user repository))
  (GET "/api/repositories" [user] (get-repositories-handler user))
  (POST "/event" request (process-event-handler  request))
  (ANY "/health_check" [] (health-check))
  (route/not-found "Not Found"))

(def app-handler (-> app-routes
                     (middleware/wrap-json-response)
                     (middleware/wrap-json-body)
                     (wrap-defaults api-defaults)))
