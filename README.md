# zeebe-echo-worker
Async Zeebe (zeebe.io) worker useful for testing.

Will complete, fail o throw an error immediately and asynchronously.

## Docker Image
```
TODO
```

#### Example Usage

## Configuration
### Job Headers
- `action`: `complete` | `fail` | `error`. Default: `complete`
- `variables`: JSON string variables to send if `action`=`complete`. Default current job variables.
- `errorCode`: the error code to throw if `action`=`error`. Default `error.code`

### Environment Variables
- `ZEEBE_CLIENT_BROKER_CONTACTPOINT`: Broker contact point **(required)**
- `ZEEBE_CLIENT_WORKER_DEFAULTTYPE`: Worker type **(required)**
- `ZEEBE_CLIENT_WORKER_THREADS`: Default 1
- `ZEEBE_CLIENT_WORKER_MAXJOBSACTIVE`: Default 32
- `ZEEBE_CLIENT_WORKER_NAME`: Default "default"
- `ZEEBE_CLIENT_JOB_TIMEOUT`: in millis, Default 5 minutes
- `ZEEBE_CLIENT_JOB_POLLINTERVAL`: in millis, Default 100 millis
- `ZEEBE_CLIENT_MESSAGE_TIMETOLIVE`: in millis, Default 1 hour
- `ZEEBE_CLIENT_REQUESTTIMEOUT`: in millis, Default 20 seconds
- Zeebe client builder overrides:
    - `PLAINTEXT_CONNECTION_VAR`: boolean, Default false
    - `ZEEBE_CA_CERTIFICATE_PATH`
    - `ZEEBE_KEEP_ALIVE`: in millis, Default 45 seconds
    - `ZEEBE_CLIENT_ID`
    - `ZEEBE_CLIENT_SECRET`
    - `ZEEBE_TOKEN_AUDIENCE`
    - `ZEEBE_AUTHORIZATION_SERVER_URL`: Default `https://login.cloud.camunda.io/oauth/token/`
    - `ZEEBE_CLIENT_CONFIG_PATH`
