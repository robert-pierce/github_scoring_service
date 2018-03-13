(ns github-scoring-service.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :refer :all]
            [github-scoring-service.handler :refer :all]
            [github-scoring-service.event_processor :as event]
            [github-scoring-service.users :as users]
            [github-scoring-service.repositories :as repos]
            [github-scoring-service.leaderboard :as leaderboard]
            [github-scoring-service.db :as db]))

(deftest api-get-user-route-test
  (testing "It correctly handles a 'get-users' request with no filter param"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 0) "some-users" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"users" "some-users"})))))
  (testing "It correctly handles a 'get-users' request with empty string filter param"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 0) "some-users" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users" {"repository" ""}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"users" "some-users"})))))
  (testing "It correctly handles a 'get-users' request with no filter param and no results"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 0) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"message" "No Results"})))))
  (testing "It correctly handles a 'get-users' request with no filter param and a thrown exception"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 0) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users"))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error")))))
  (testing "It correctly handles a 'get-users' request with a filter param"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 1) "some-users" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"repository" "some-repository" "users" "some-users"})))))
  (testing "It correctly handles a 'get-users' request with a filter param and no results"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 1) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"repository" "some-repository" "message" "No Results"})))))
  (testing "It correctly handles a 'get-users' request with a filter param and a thrown exception"
    (with-redefs [users/get-users (fn [& args] (if (= (count args) 1) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users" {"repository" "some-repository"}))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error"))))))

(deftest api-get-repositories-route-test
  (testing "It correctly handles a 'get-repositories' request with no filter param"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 0) "some-repositories" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"repositories" "some-repositories"})))))
  (testing "It correctly handles a 'get-repositories' request with empty string filter param"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 0) "some-repositories" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories" {"user" ""}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"repositories" "some-repositories"})))))
  (testing "It correctly handles a 'get-repositories' request with no filter param and no results"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 0) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"message" "No Results"})))))
  (testing "It correctly handles a 'get-repositories' request with no filter param and a thrown exception"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 0) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories"))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error")))))
  (testing "It correctly handles a 'get-repositories' request with a filter param"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 1) "some-repositories" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories" {"user" "some-user"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "repositories" "some-repositories"})))))
  (testing "It correctly handles a 'get-repositories' request with a filter param and no results"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 1) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories" {"user" "some-user"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "message" "No Results"})))))
  (testing "It correctly handles a 'get-repositories' request with a filter param and a thrown exception"
    (with-redefs [repos/get-repositories (fn [& args] (if (= (count args) 1) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/repositories" {"user" "some-user"}))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error"))))))

(deftest api-get-user-score-route-test
  (testing "It correctly handles a 'get-user-score' request with no filter param"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 1) "42" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "score" "42"})))))
  (testing "It correctly handles a 'get-user-score' request with empty string filter param"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 1) "42" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score" {"repository" ""}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "score" "42"})))))
  (testing "It correctly handles a 'get-user-score' request with no filter param and no results"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 1) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "message" "Could Not Get Score For This User"})))))
  (testing "It correctly handles a 'get-users' request with no filter param and a thrown exception"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 1) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score"))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error")))))
  (testing "It correctly handles a 'get-user-score' request with a filter param"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 2) "42" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "repository" "some-repository" "score" "42"})))))
  (testing "It correctly handles a 'get-user-score' request with a filter param and no results"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 2) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "repository" "some-repository" "message" "Could Not Get Score For This User And Repository"})))))
  (testing "It correctly handles a 'get-user-score' request with a filter param and a thrown exception"
    (with-redefs [users/get-user-score (fn [& args] (if (= (count args) 2) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/users/some-user/score" {"repository" "some-repository"}))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error"))))))

(deftest api-get-user-history-route-test
  (testing "It correctly handles a 'get-user-history' request with no filter param"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 1) "some-history" "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "score" "42" "history" "some-history"})))))
  (testing "It correctly handles a 'get-user-history' request with empty string filter param"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 1) "some-history" "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history" {"repository" ""}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "score" "42" "history" "some-history"})))))
  (testing "It correctly handles a 'get-user-history' request with no filter param and no results"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 1) nil "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "message" "No History For This User"})))))
  (testing "It correctly handles a 'get-user-score' request with no filter param and a thrown exception"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 1) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history"))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error")))))
  (testing "It correctly handles a 'get-user-score' request with a filter param"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 2) "some-history" "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "repository" "some-repository" "score" "42" "history" "some-history"})))))
  (testing "It correctly handles a 'get-user-score' request with a filter param and no results"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 2) nil "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"user" "some-user" "repository" "some-repository" "message" "No History For This User And Repository"})))))
  (testing "It correctly handles a 'get-user-score' request with a filter param and a thrown exception"
    (with-redefs [users/get-user-history (fn [& args] (if (= (count args) 2) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))
                  users/get-user-score (fn [& args] "42")]
      (let [response (app-handler (mock/request :get "/api/users/some-user/history" {"repository" "some-repository"}))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error"))))))

(deftest api-get-leaderboard-route-test
  (testing "It correctly handles a 'get-leaderboard' request with no filter param"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 0) "some-leaderboard" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"leaderboard" "some-leaderboard"})))))
  (testing "It correctly handles a 'get-leaderboard' request with empty string filter param"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 0) "some-leaderboard" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard" {"repository" ""}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"leaderboard" "some-leaderboard"})))))
  (testing "It correctly handles a 'get-users' request with no filter param and no results"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 0) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard"))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"message" "No Leaderboard Results"})))))
  (testing "It correctly handles a 'get-users' request with no filter param and a thrown exception"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 0) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard"))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error")))))
  (testing "It correctly handles a 'get-leadergoard' request with a filter param"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 1) "some-leaderboard" "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"repository" "some-repository" "leaderboard" "some-leaderboard"})))))
  (testing "It correctly handles a 'get-users' request with a filter param and no results"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 1) nil "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard" {"repository" "some-repository"}))]
        (is (= (:status response) 200))
        (is (= (parse-string (:body response)) {"repository" "some-repository" "message" "No Leaderboard Results For This Repository"})))))
  (testing "It correctly handles a 'get-users' request with a filter param and a thrown exception"
    (with-redefs [leaderboard/get-leaderboard (fn [& args] (if (= (count args) 1) (throw (Exception. "some-exception")) "some-incorrect-arg-count"))]
      (let [response (app-handler (mock/request :get "/api/leaderboard" {"repository" "some-repository"}))]
        (is (= (:status response) 500))
        (is (= (:body response) "There was a server error"))))))

(deftest health-check-route-test
  (testing "It correctly handles a health-check request"
    (let [response (app-handler (mock/request :get "/health_check"))]
      (is (= (:status response) 200))
      (is (= (:body response) "I'm Alive")))))

(deftest not-found-route-test
  (testing "It correctly handles a route that is not found"
    (let [response (app-handler (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
