# Github Scoring Service

The Github Scoring Service is a service that will keep track of github activity and will assign a score to users of a repository based off of events triggered by a users activity. 

At its heart, the Github Scoring Service is a simple HTTP server that listens for events pushed by a github webhook. Once received, an event is persisted in a database. 

Users can then query the Github Scoring Service for information about the scores of certain users. 

See the API section below for a detailed overview of the API. 

***
## Running Locally

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

>`docker-compose up` 

if you want the service to start in the foreground 
(usefull for inspecting logs as the app is running)

or 

>`docker-compose up -d` 

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
If you are running the application locally then you may want to send mock requests to the service in order to simulate a live github webhook. 

>You can set a locally running instance of the app to accept live github webhooks. If you are interested in doing this then see the section below on setting up live github webhooks. 

In order to send mock events you will need to check out the API for the [Github Mock Event Emitter](https://github.com/robert-pierce/github_mock_event_emitter).

If you started the app with docker-compose then you should be able to reach the Github Mock Event Emitter by sending requests to 

>`127.0.0.1:8010`

***

If you would like to compile this source code directly then you will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

To start a web server for the application run:

    lein ring server-headless

The app should start up on port 8000

***
### Setting Up Live Github Webhooks

In order to run the app to accept live github webhooks then you need to deploy it in a way that it can receive POST requests from the internet. 

You will also need to set a MySQL database up by setting the environment vairables (mentioned below) and deploying it somwhere that the scoring service can connect to it. 

A fantastic way to do all this is with [Heroku](https://www.heroku.com)

Once the app is properly deployed all you need to do is to go to the setting of a git repository and set up the webhook url (if you have permissions) to point to your deployed app. 

Github will then send a POST request to your app to verify the webhook. If done correctly the app will verify the webhook (by responding with a 200), and the app will start recoding events.

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
### Making Requests
If you start the app via docker-compose then you should be able to reach the Github Scoring service by sending requests to the following url

>`127.0.0.1:8000`

You can reach the Mock Github Event Emitter at:

>`127.0.0.1:8010`

You should also be able to connect to the database with the following:

>`Host:127.0.0.1`

>`username:test`

> `password:test`

> `database: github_scoring_service`

> `port:3306`


***
## API

- **Get a list of distinct users**
    If you want a list of all distinct users that have triggered github webhooks that have been pushed to this service then
    make a **GET** request to the following endpoint:
    
    > `/api/users`
    
    If you want to filter these results by repository then pass in a query param like so:
    
     > `/api/users?repository=<some-repo-name>`
     
- **Get a list of distinct repositories**
    If you want a list of all distinct repositories that have had users trigger github webhooks that have been pushed to this  service then make a **GET** request to the following endpoint:
    
    > `/api/repositories`
    
    If you want to filter these results by user then pass in a query param like so:
    
     > `/api/repositories?users=<some-user-name>`
     
- **Get a user's score**
    If you want to get a user's score then make a **GET** request to the following endpoint:
    
    > `/api/users/:users/score`
    
    where `:users` is a user name.
    
    If you want to filter these results by repository then pass in a query param like so:
    
     > `/api/users/:users/score?repository=<some-repo-name>`    
     
- **Get a user's history**
    If you want to get a user's history then make a **GET** request to the following endpoint:
    
    > `/api/users/:users/history`
    
    where `:users` is a user name.
    
    If you want to filter these results by repository then pass in a query param like so:
    
     > `/api/users/:users/history?repository=<some-repo-name>`    

- **Get the leaderboard**
    If you want to get a list of users ranked from highest score to lowest score then make a **GET** request to the following endpoint:
    
    > `/api/leaderboard`
     
    If you want to filter these results by repository then pass in a query param like so:
    
     > `/api/leaderboard?repository=<some-repo-name>`  
     
- **Health Check**
    If you want to query the app for a health check then make a **GET** request to the following endpoint:
    
    > `/health_check`
