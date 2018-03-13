(ns github-scoring-service.query-test
  (:require [clojure.test :refer :all]
            [github-scoring-service.query :as query]))

(deftest get-user-query-test
  (testing "It correctly handles no params"
    (is (= "SELECT DISTINCT sender FROM events" (query/get-users-query))))
  (testing "It correctly handles one param"
    (is (= "SELECT DISTINCT sender FROM events WHERE repository=\"some-repository\"" (query/get-users-query "some-repository")))))

(deftest get-repositories-query-test
  (testing "It correctly handles no params"
    (is (= "SELECT DISTINCT repository FROM events" (query/get-repositories-query))))
  (testing "It correctly handles one param"
    (is (= "SELECT DISTINCT repository FROM events WHERE sender=\"some-user\"" (query/get-repositories-query "some-user")))))

(deftest get-user-score-query-test
  (testing "It correctly handles one param"
    (is (= "SELECT SUM(point_value) FROM events WHERE sender=\"some-user\"" (query/get-user-score-query "some-user"))))
  (testing "It correctly handles two params"
    (is (= "SELECT SUM(point_value) FROM events WHERE sender=\"some-user\" AND repository=\"some-repository\"" (query/get-user-score-query "some-user" "some-repository")))))

 (deftest get-user-history-query-test
  (testing "It correctly handles one param"
    (is (=  "SELECT type, point_value, repository, time_stamp FROM events WHERE sender=\"some-user\"" (query/get-user-history-query "some-user"))))
  (testing "It correctly handles two params"
    (is (= "SELECT type, point_value, repository, time_stamp FROM events WHERE sender=\"some-user\" AND repository=\"some-repository\"" (query/get-user-history-query "some-user" "some-repository")))))

(deftest get-leaderboard-query-test
  (testing "It correctly handles no params"
    (is (= "SELECT sender as user, SUM(point_value) as points FROM events GROUP BY sender ORDER BY points DESC" (query/get-leaderboard-query))))
  (testing "It correctly handles one param"
    (is (= "SELECT sender as user, SUM(point_value) as points FROM events WHERE repository=\"some-repository\" GROUP BY sender ORDER BY points DESC" (query/get-leaderboard-query "some-repository")))))
