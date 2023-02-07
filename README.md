# Job Processor
Job Processing [Spring Boot](http://projects.spring.io/spring-boot/) service that sorts jobs in order of their respective prerequisites
## About the app

A job is a collection of tasks, where each task has a name and a shell command. Tasks may
depend on other tasks and require that those are executed beforehand. The service takes care
of sorting the tasks to create a proper execution order.

## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)


## Running the application locally

You can run the application locally using the gradle wrapper which is included in the project. 
It can be run from the project's base directory like so:

```shell
./gradlew bootRun  
```
After running it, the application should be running on port 8080. 

To test the application, you can run the following command from the shell:

```shell
curl -d @mytasks.json http://localhost:8080/tasks/sort-commands -H "Content-Type: application/json" | bash
```

The application can also be run in debug mode which exposes port 5005 for debugging like so:

```shell
./gradlew bootRun --debug-jvm
```

After a remote debugger is attached to the application using the aforementioned port, the application will start.

## API Endpoints
| HTTP Verbs | Endpoints | Action |
| -- | --- | --- |
| POST | /tasks/sort | Sort the tasks and return the result in JSON |
| POST | /tasks/sort-commands | Sort the tasks and return the commands as a stream of bytes in the form of a bash script |


## License
This project is available for use under the MIT License.