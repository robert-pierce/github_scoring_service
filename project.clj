(defproject github_scoring_service "0.1.0-SNAPSHOT"
  :description "This is a service that tracks user scores based off points received by various github webhooks"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [clj-log "0.4.6"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [environ "1.0.2"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.0.2"]]
  :ring {:handler github-scoring-service.handler/app-handler
         :port 8000
         :init github-scoring-service.init/init-app})
