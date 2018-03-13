(ns github-scoring-service.repositories-test
  (:require [clojure.test :refer :all]
            [github-scoring-service.repositories :as repositories]
            [github-scoring-service.db :as db]))

(deftest get-repositories-test
  (testing "It correctly handles no params and good results from db"
    (with-redefs [db/get-repositories (fn [& args] (if (= (count args) 0) [{:repo true}] [{:repo false}]))]
      (is (= true (first (repositories/get-repositories))))))
  (testing "It correctly handles one param and good results from db"
    (with-redefs [db/get-repositories (fn [& args] (if (= (count args) 1) [{:repo true}] [{:repo false}]))]
      (is (= true (first (repositories/get-repositories "some-param"))))))
  (testing "It correctly handles no params and exception from db"
    (with-redefs [db/get-repositories (fn [& args] (if (= (count args) 0) (throw (Exception. "some-exception")) [false]))]
      (try  
        (repositories/get-repositories)
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e))))))
  (testing "It correctly handles one param and exception from db"
    (with-redefs [db/get-repositories (fn [& args] (if (= (count args) 1) (throw (Exception. "some-exception")) [false]))]
      (try  
        (repositories/get-repositories "some-param")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e)))))))
