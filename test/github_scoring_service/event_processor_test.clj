(ns github-scoring-service.event_processor_test
  (:require [clojure.test :refer :all]
            [github-scoring-service.event_processor :as event_processor]
            [github-scoring-service.db :as db]))

(def mock-value-map {"push" 5 "pull_request_review_comment" 4 "watch" 3 "create" 2})
(def mock-request {:headers {"x-github-event" "some-event-type" "x-github-delivery" "some-guid"}
                   :body {"sender" {"login" "some-user"} "repository" {"full_name" "some-repository"}}})

(deftest get-event-point-value-test
 (testing "It correctly handles a push event type"
   (with-redefs [event_processor/value-map mock-value-map]
     (is (= 5 (event_processor/get-event-point-value "push")))))
 (testing "It correctly handles a pull_request_review_comment event type"
   (with-redefs [event_processor/value-map mock-value-map]
     (is (= 4 (event_processor/get-event-point-value "pull_request_review_comment")))))
 (testing "It correctly handles a watch event type"
   (with-redefs [event_processor/value-map mock-value-map]
     (is (= 3 (event_processor/get-event-point-value "watch")))))
 (testing "It correctly handles a create event type"
   (with-redefs [event_processor/value-map mock-value-map]
     (is (= 2 (event_processor/get-event-point-value "create")))))
 (testing "It correctly handles some other event type"
   (with-redefs [event_processor/value-map mock-value-map]
     (is (= 1 (event_processor/get-event-point-value "some-other-event"))))))

(deftest process-event-test
  (testing "It correctly handles a request"
    (with-redefs [event_processor/get-event-point-value (fn [& args] 42)
                  db/persist-event! (fn [guid event-type event-value github-user repository]
                                      {:guid guid :event-type event-type :event-value 
                                       event-value :github-user github-user :repository repository})]  
      (is (= {:guid "some-guid" :event-type "some-event-type" :event-value 42 
              :github-user "some-user" :repository "some-repository"}
             (event_processor/process-event mock-request))))))
