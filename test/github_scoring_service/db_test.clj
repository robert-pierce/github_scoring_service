(ns github-scoring-service.db_test
  (:require [clojure.test :refer :all]
            [github-scoring-service.db :as db]
            [github-scoring-service.config :as c]
            [github-scoring-service.query :as query]
            [clojure.java.jdbc :as sql]))

(def mock-db {:classname "some-sql-driver"
              :subprotocol "some-sql-protocol"
              :subname "some-db-url"
              :user "some-db-user"
              :password "some-db-password"})

(deftest show-table-test
  (testing "It correctly invokes a 'show tables' query"
    (with-redefs [db/db mock-db
                  sql/query (fn [db query] (first query))]
      (is (= "show tables" (db/show-tables))))))

(deftest persist-event!-test
  (testing "It correctly invokes a 'persist event!' query"
    (with-redefs [db/db mock-db
                  sql/insert! (fn [db table row] row)]
      (is (= (struct db/events-row "some-id" "some-type" "some-point-value" "some-sender" "some-repository") 
             (db/persist-event! "some-id" "some-type" "some-point-value" "some-sender" "some-repository"))))))

(deftest get-users-test
  (testing "It correctly invokes a 'get users' query with no params"
    (with-redefs [db/db mock-db
                  query/get-users-query (fn [& args] (if (= (count args) 0) "some-get-users-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-users-query" (db/get-users)))))
  (testing "It correctly invokes a 'get users' query with one param"
    (with-redefs [db/db mock-db
                  query/get-users-query (fn [& args] (if (= (count args) 1) "some-get-users-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-users-query" (db/get-users "some-param"))))))

(deftest get-repositories-test
  (testing "It correctly invokes a 'get repositories' query with no params"
    (with-redefs [db/db mock-db
                  query/get-repositories-query (fn [& args] (if (= (count args) 0) "some-get-repositories-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-repositories-query" (db/get-repositories)))))
  (testing "It correctly invokes a 'get repositories' query with one param"
    (with-redefs [db/db mock-db
                  query/get-repositories-query (fn [& args] (if (= (count args) 1) "some-get-repositories-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-repositories-query" (db/get-repositories "some-param"))))))

(deftest get-user-score-test
  (testing "It correctly invokes a 'get user score' query with one param"
    (with-redefs [db/db mock-db
                  query/get-user-score-query (fn [& args] (if (= (count args) 1) "some-get-user-score-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-user-score-query" (db/get-user-score "some-param")))))
  (testing "It correctly invokes a 'get user score' query with two params"
    (with-redefs [db/db mock-db
                  query/get-user-score-query (fn [& args] (if (= (count args) 2) "some-get-user-score-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-user-score-query" (db/get-user-score "some-param" "some-other-param"))))))

(deftest get-user-history-test
  (testing "It correctly invokes a 'get user history' query with one param"
    (with-redefs [db/db mock-db
                  query/get-user-history-query (fn [& args] (if (= (count args) 1) "some-get-user-history-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-user-history-query" (db/get-user-history "some-param")))))
  (testing "It correctly invokes a 'get user score' query with two params"
    (with-redefs [db/db mock-db
                  query/get-user-history-query (fn [& args] (if (= (count args) 2) "some-get-user-history-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-user-history-query" (db/get-user-history "some-param" "some-other-param"))))))

(deftest get-leaderboard-test
  (testing "It correctly invokes a 'get leaderboard' query with no params"
    (with-redefs [db/db mock-db
                  query/get-leaderboard-query (fn [& args] (if (= (count args) 0) "some-get-leaderboard-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-leaderboard-query" (db/get-leaderboard)))))
  (testing "It correctly invokes a 'get users' query with one param"
    (with-redefs [db/db mock-db
                  query/get-leaderboard-query (fn [& args] (if (= (count args) 1) "some-get-leaderboard-query" "incorrect-param-count"))
                  sql/query (fn [db query] (first query))]
      (is (= "some-get-leaderboard-query" (db/get-leaderboard "some-param"))))))
