# WAPP

This document explains how to compile and run this project.

## Compile & Run

This is a Java 8 Spring Boot project meant to be built by maven.
1. Requirements: Install the latest Java 8 compatible SDK and Maven tool
2. execute **'mvnw install'** (without quotes) to build and run all Unit and Integration tests
3. execute **'mvnw spring-boot:run'** to run the server in localhost:8090/

To change the server port, edit application.yaml server.port variable.

note: Java 8 has been chosen for having the broadest compatibility base.

## DB restore

This app uses H2 file based persistence for DB operations and Flyway for DB migrations.

To restore the app to it's initial state, simply delete the data folder in the project root

If you want to run basic queries against the H2 DB, run the app and go to [H2 console](http://localhost:8090/h2-console/).


## API description
There are three endpoints exposed for Diff purposes:

### Submit Left Diff text
#### root path: 
```
POST <host>/v1/diff/<id>/left
```
#### POST:
   Creates or updates the left text of a diff with the given valid JSON base64 encoded binary data. 
   
   ID must be a long value.

Example request:
```
ewoicmVzb3VyY2UiOiAiYWJjZGVmZzEyMy0tLS0iLAoidGFyZ2V0IjogImFiY2RlZmctLS0tMTIzIiwKInRlc3QiOiA2MAp9
```
Example response:
```
{
    "message": "Left diff accepted",
    "data": {
        "id": "b7b5c61a-0ee9-4d28-ba85-4be77a32362e",
        "diffId": 2,
        "leftText": "{\n\"resource\": \"abcdefg123----\",\n\"target\": \"abcdefg----123\",\n\"test\": 60\n}",
        "rightText": null
    }
}
```

### Submit Right Diff text
#### root path: 
```
POST <host>/v1/diff/<id>/left
```
#### POST:
   Creates or updates the right text of a diff with the given valid JSON base64 encoded binary data. 
   
   ID must be a long value.

Example request:
```
ewoicmVzb3VyY2UiOiAiYWJjZGVmZzEyM3h4LS0iLAoidGFyZ2V0IjogImFiY2RlZmctLS0tMTIzIiwKInRlc3QiOiA2MAp9
```
Example response:
```
{
    "message": "right diff accepted",
    "data": {
        "id": "23e3a90a-5636-42fe-b802-f60f0da04cf1",
        "diffId": 4,
        "leftText": "{\n\"resource\": \"abcdefg123----\",\n\"target\": \"abcdefg----123\",\n\"test\": 60\n}",
        "rightText": "{\n\"resource\": \"abcdefg123xx--\",\n\"target\": \"abcdefg----123\",\n\"test\": 60\n}"
    }
}
```

### Calculate Diff
#### root path: 
```
GET <host>/v1/diff/<id>
```
#### GET:
   Compares and calculates the diff for the given ID.
   
   ID must be a long value. Both left and right text must exist.

Example request:
```
GET <host>/v1/diff/4
```
Example response:
```
{
    "message": "Provided strings have diffs.",
    "data": [
        {
            "offset": 25,
            "length": 2
        }
    ]
}
```

## Author

Ahmet APAYDIN