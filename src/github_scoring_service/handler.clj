(ns github-scoring-service.handler
  (:require [clojure.string :as str] 
            [compojure.core :refer :all]
            [compojure.route :as route]
            [github-scoring-service.event_processor :as event]
            [github-scoring-service.users :as users]
            [github-scoring-service.repositories :as repos]
            [github-scoring-service.leaderboard :as leaderboard]
            [github-scoring-service.db :as db]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :as middleware]))

(defn get-users-handler
  "Handles a get-users request"
  [repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (try 
      (if-let [users (users/get-users repository)]
        {:status 200 :body {:repository repository :users users}}
        {:status 200 :body {:repository repository :message "No Results"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))
    (try
      (if-let [users (users/get-users)]
        {:status 200 :body {:users users}}
        {:status 200 :body {:message "No Results"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))))

(defn get-repositories-handler
  "Handlers a get-repositories request"
  [user]
  (if (and (not (nil? user)) (not (str/blank? user)))
    (try
      (if-let [repos (repos/get-repositories user)]
        {:status 200 :body {:user user :repositories repos}}
        {:status 200 :body {:user user :message "No Results"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))
    (try 
      (if-let [repos (repos/get-repositories)]
        {:status 200 :body {:repositories repos}}
        {:status 200 :body {:message "No Results"}})
      (catch Exception e 
        {:status 500 :body "There was a server error"}))))

(defn get-user-score-handler
  "Handlers a get-user-score request"
  [user repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (try
      (if-let [score (users/get-user-score user repository)]
        {:status 200 :body {:user user :repository repository :score score}}
        {:status 200 :body {:user user :repository repository :message "Could Not Get Score For This User And Repository"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))
    (try
      (if-let [score (users/get-user-score user)]
        {:status 200 :body {:user user :score score}}
        {:status 200 :body {:user user :message "Could Not Get Score For This User"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))))

(defn get-user-history-handler
  "Handlers a get-user-history request"
  [user repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (try
      (if-let [history (users/get-user-history user repository)]
        (if-let [score (users/get-user-score user repository)]
          {:status 200 :body {:user user :repository repository :score score :history history}}
          {:status 200 :body {:user user :history history :message "Could Not Get User Score"}})
        {:status 200 :body {:user user :repository repository :message "No History For This User And Repository"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))
    (try
      (if-let [history (users/get-user-history user)]
        (if-let [score (users/get-user-score user)] 
          {:status 200 :body {:user user :score score :history history}}
          {:status 200 :body {:user user :history history :message "Could Not Get User Score"}})
        {:status 200 :body {:user user :message "No History For This User"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))))

(defn get-leaderboard-handler
  "Handles a get-leaderboard request"
  [repository]
  (if (and (not (nil? repository)) (not (str/blank? repository)))
    (try
      (if-let [leaderboard (leaderboard/get-leaderboard repository)]
        {:status 200 :body {:repository repository :leaderboard leaderboard}}
        {:status 200 :body {:repository repository :message "No Leaderboard Results For This Repository"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))
    (try
      (if-let [leaderboard (leaderboard/get-leaderboard)]
        {:status 200 :body {:leaderboard leaderboard}}
        {:status 200 :body {:message "No Leaderboard Results"}})
      (catch Exception e
        {:status 500 :body "There was a server error"}))))

(defn process-event-handler
  "Handles a process-event request"
  [request]
  (event/process-event request)
  {:status 200})

(defn health-check
  "A health check endpoint that will also print the git hash"
  []
  {:status 200 :body "I'm Alive"})

(defroutes app-routes
  (GET "/api/users" [repository] (get-users-handler repository))
  (GET "/api/users/:user/score" [user repository] (get-user-score-handler user repository))
  (GET "/api/users/:user/history" [user repository] (get-user-history-handler user repository))
  (GET "/api/leaderboard" [repository] (get-leaderboard-handler repository))
  (GET "/api/repositories" [user] (get-repositories-handler user))
  (POST "/event" request (process-event-handler request))
  (ANY "/health_check" [] (health-check))
  (route/not-found "Not Found"))

(def app-handler (-> app-routes
                     (middleware/wrap-json-response)
                     (middleware/wrap-json-body)
                     (wrap-defaults api-defaults)))
