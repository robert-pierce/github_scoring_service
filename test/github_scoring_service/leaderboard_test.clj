(ns github-scoring-service.leaderboard-test
  (:require [clojure.test :refer :all]
            [github-scoring-service.leaderboard :as leaderboard]
            [github-scoring-service.db :as db]))

(deftest get-leaderboard-test
  (testing "It correctly handles no params and good results from db"
    (with-redefs [db/get-leaderboard (fn [& args] (if (=  (count args) 0) [true] [false]))]
      (is (= true (first (leaderboard/get-leaderboard))))))
  (testing "It correctly handles one param and good results from db"
    (with-redefs [db/get-leaderboard (fn [& args] (if (=  (count args) 1) [true] [false]))]
      (is (= true (first (leaderboard/get-leaderboard "some-param"))))))
  (testing "It correctly handles no params and exception from db"
    (with-redefs [db/get-leaderboard (fn [& args] (if (=  (count args) 0) (throw (Exception. "some-exception")) [false]))]
      (try  
        (leaderboard/get-leaderboard)
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e))))))
  (testing "It correctly handles one param and exception from db"
    (with-redefs [db/get-leaderboard (fn [& args] (if (=  (count args) 1) (throw (Exception. "some-exception")) [false]))]
      (try  
        (leaderboard/get-leaderboard "some-param")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e)))))))
