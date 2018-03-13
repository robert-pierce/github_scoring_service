(ns github-scoring-service.users-test
  (:require [clojure.test :refer :all]
            [github-scoring-service.users :as users]
            [github-scoring-service.db :as db]))



  (deftest get-users-test
  (testing "It correctly handles no params and good results from db"
    (with-redefs [db/get-users (fn [& args] (if (=  (count args) 0)  [{:repo true}] [[:repo false]]))]
      (is (= true (first (users/get-users))))))
  (testing "It correctly handles one param and good results from db"
    (with-redefs [db/get-users (fn [& args] (if (=  (count args) 1) [{:repo true}] [{:repo false}]))]
      (is (= true (first (users/get-users "some-param"))))))
  (testing "It correctly handles no params and exception from db"
    (with-redefs [db/get-users (fn [& args] (if (=  (count args) 0) (throw (Exception. "some-exception")) [false]))]
      (try  
        (users/get-users)
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e))))))
  (testing "It correctly handles one param and exception from db"
    (with-redefs [db/get-users (fn [& args] (if (=  (count args) 1) (throw (Exception. "some-exception")) [false]))]
      (try  
        (users/get-users "some-param")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e)))))))

(deftest get-user-score-test
  (testing "It correctly handles one param and good results from db"
    (with-redefs [db/get-user-score (fn [& args] (if (=  (count args) 1)  [{:repo true}] [{:repo false}]))]
      (is (= true (users/get-user-score "some-user")))))
  (testing "It correctly handles two params and good results from db"
    (with-redefs [db/get-user-score (fn [& args] (if (=  (count args) 2) [{:repo true}] [{:repo false}]))]
      (is (= true (users/get-user-score "some-param" "some-repo")))))
  (testing "It correctly handles one param and exception from db"
    (with-redefs [db/get-user-score (fn [& args] (if (=  (count args) 1) (throw (Exception. "some-exception")) [{:repo false}]))]
      (try  
        (users/get-user-score "some-user")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e))))))
  (testing "It correctly handles two params and exception from db"
    (with-redefs [db/get-user-score (fn [& args] (if (=  (count args) 2) (throw (Exception. "some-exception")) [{:repo false}]))]
      (try  
        (users/get-user-score "some-user" "some-repo")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e)))))))

(deftest get-user-history-test
  (testing "It correctly handles one param and good results from db"
    (with-redefs [db/get-user-history (fn [& args] (if (=  (count args) 1)  [true] [false]))]
      (is (= true (first (users/get-user-history "some-user"))))))
  (testing "It correctly handles two params and good results from db"
    (with-redefs [db/get-user-history (fn [& args] (if (=  (count args) 2) [true] [false]))]
      (is (= true (first (users/get-user-history "some-param" "some-repo"))))))
  (testing "It correctly handles one param and exception from db"
    (with-redefs [db/get-user-history (fn [& args] (if (=  (count args) 1) (throw (Exception. "some-exception")) [{:repo false}]))]
      (try  
        (users/get-user-history "some-user")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e))))))
  (testing "It correctly handles two params and exception from db"
    (with-redefs [db/get-user-history (fn [& args] (if (=  (count args) 2) (throw (Exception. "some-exception")) [{:repo false}]))]
      (try  
        (users/get-user-history "some-user" "some-repo")
        (is (= 1 2))
        (catch Exception e
          (is (instance? Exception e)))))))

