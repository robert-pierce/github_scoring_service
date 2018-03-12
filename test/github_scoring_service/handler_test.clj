(ns github-scoring-service.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [github-scoring-service.handler :refer :all]))

(deftest test-app
  (testing "health-check route"
    (let [response (app-handler (mock/request :get "/health_check"))]
      (is (= (:status response) 200))
      (is (= (:body response) "I'm Alive"))))

  (testing "not-found route"
    (let [response (app-handler (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
