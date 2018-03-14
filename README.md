# github_scoring_service

The github_scoring_service is a service that listens for events are sent by a Github webhook. The service will assign a point value based on certain github events, and save 

***
## Running
This service is intended to be run in conjunction with the [github-scoring-service](https://github.com/robert-pierce/github_scoring_service). See the documentation for the [github-scoring-service](https://github.com/robert-pierce/github_scoring_service) for more information on running the service in this fashion.


If you would like to compile this source code directly then you will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

To start a web server for the application, run:

    lein ring server-headless

The app should start up on port 8010

### Environment
The service only needs one environment variable in order to function:
    
    SCORING-SERVICE-URL=
    
which, as the name suggests, is the URL to the github_scoring_service that this service will push events too via HTTP.
If you run this service in conjunction with the github_scoring_service, as intended, then you will not need to worry about setting the environment because the docker command will handle that for us.

If you wish to run the service as a stand alone application then you can set the environment in your profiles.clj if compiling with Leiningen.

***
## API
There are two ways of interacting with the github_mock_event_emitter.

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
    
    This will cause a number of events to be pushed to the github_scoring_service. 
    
    The total number of events is equal to the value passed in the POST body for the _number_of_events_ key. 
    
    For each event an event-type, user, and repository are selected at random from the entries in JSON arrays passed in the POST body for the respective entries.
