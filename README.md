# Github Scoring Service

The Github Scoring Service is a service that will keep track of github activity and will assign a score to users of a repository based off of events triggered by a users activity. 

At its heart, the Github Scoring Service is a simple HTTP server that listens for events pushed by a github webhook. Once received, an event is persisted in a database. 

Users can then query the Github Scoring Service for information about the scores of certain users. 

See the API section below for a detailed overview of the API. 

***
## Running

The easiest way to run the application is by using docker-compose. If you don't have docker and docker-compose installed on your maching then you may want to consider doing so. Check out the following resources:

[Install Docker](https://docs.docker.com/install)

[Install docker-compose](https://docs.docker.com/compose/install)


**The Github Scoring Service** has three parts
1. The scoring service (this repo)
2. A MySql database 
3. [A mock event emitter](https://github.com/robert-pierce/github_mock_event_emitter) which will push simulated events to the scoring service.

In order to locally start all three parts in conjunction follow these steps:


1. Clone this repository onto your local machine
2. On your local machine make sure all docker-containers running on ports 8000, 8010, and 3306 are stopped.
3. In the project root directory run one of the following commands:

`docker-compose up` 

if you want the service to start in the foreground 
(usefull for inspecting logs as the app is running)

or 

`docker-compose up -d` 

if you want the service to start in the background.

The app should then take a couple of minutes to download all the source code, compile it, and run it. This delay will be much shorter on subsequent starts after the intial containers are built.

If successful, after docker finishes building all of the containers and invoking them you should see three services up and running when you run the following command:

`docker-compose ps`


| Name                                 | Command                     | State | Ports                              | 
| ------------------------------------ |:---------------------------:| -----:| -----------------------------------
| githubscoringservice_database_1      |  /entrypoint.sh mysqld      | Up    | 0.0.0.0:3306->3306/tcp, 33060/tcp
| githubscoringservice_event-emitter_1 | java -jar app-standalone.ja | Up    | 0.0.0.0:8010->8010/tcp
| githubscoringservice_score-keeper_1  | java -jar app-standalone.ja | Up    | 0.0.0.0:8000->8000/tcp


Notice the port mappings, we will be using these to call our service.
***

If you would like to compile this source code directly then you will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

To start a web server for the application run:

    lein ring server-headless

The app should start up on port 8000

***
### Environment
Environment

The service only needs a few environment variables in order to function:

>`DB-USERNAME=test`

>`DB-PASSWORD=test`

>`DB-URL=database:3306/github_scoring_service`

>`PUSH-EVENT-VALUE=5`

>`PR-COMMENT-VALUE=4`

>`WATCH-EVENT-VALUE=3`

>`CREATE-EVENT-VALUE=2`

However, if you start the app via docker-compose you don't need to worry about setting any environment variables. 

If you would like to run the service independent of docker then you will need to set the variables in your profiles.clj.

***
## API

1. **You can trigger individual events to be push to the github_scoring_service**
  
    To trigger individual events you need to send a **POST** request to the following endpoint:
     
     `/event`
     
     with a JSON POST body of the following form:
     ```json
       {
          "type": "<some-event-type-name>",
          "user": "<some-user-name>",
          "repository": "<some-repository-name>"
       }
    ```
 
2. **You can trigger a batch of events to be pushed to the github_scoring_service with one request**
    
    It can be a real pain to have to trigger enough event manually using the `/event` endpoint. 
    
    To trigger many events at once you need to send a **POST** request to the following endpoint:
     
     `/simulator`
     
     with a JSON POST body of the following form:
     ```json
       {
          "number_of_events": 42,
          "types": ["<some-event-type-name>", "<some-other-event-type-name>", ...],
          "users": ["<some-user-name>", "<some-other-user-name>", ... ],
          "repositories": ["<some-repository-name>", "<some-other-repository-name>", ...]
       }
    ```
