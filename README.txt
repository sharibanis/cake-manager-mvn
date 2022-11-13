SOLUTION README:
=================
1. The solution is based on Java 15/Maven/SpringBoot REST API
2. The REST service is built upon Springboot
	a. The CakeApplication class automatically uploads all the cakes into the HSQL DB upon startup
	b. The cakes.URL path is set in /cake-manager-mvn/src/main/resources/application.properties file as:
		cakes.URL=https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json
	c. The list of all cakes are obtained using @GetMapping i.e. HTTP GET using the getCakes() and getAllCakes() methods
	d. The endpoints for these methods are "/" (getCakes()) and "/cakes" (getAllCakes())
3. Java based HSQL DB in-memory database is used to store and process data 
4. The unit tests are written in JUnit
5. The tests can be run by the command "mvn clean test"
6. The application can be run by 
	a. First using the "mvn clean package" to build the war file	
	b. This will create ..\eclipse-workspace\dbRESTService\target\dbRESTService-0.0.1-SNAPSHOT.war
	c. Next using the "mvn spring-boot:run" to deploy the war file on tomcat on localhost:8282
	d. Use a REST client like Swagger or Postman to access the service
7. To skip tests and run use: `mvn -DskipTests clean package && mvn -DskipTests spring-boot:run`
8. Swagger is running at http://localhost:8282/swagger-ui.html#
9. Authentication via OAuth2 is done using Github account

========================================================================================================

Cake Manager Micro Service (fictitious)
=======================================

A summer intern started on this project but never managed to get it finished.
The developer assured us that some of the above is complete, but at the moment accessing the /cakes endpoint
returns a 404, so getting this working should be the first priority.

Requirements:
* By accessing the root of the server (/) it should be possible to list the cakes currently in the system. This must be presented in an acceptable format for a human to read.

* It must be possible for a human to add a new cake to the server.

* By accessing an alternative endpoint (/cakes) with an appropriate client it must be possible to download a list of
the cakes currently in the system as JSON data.

* The /cakes endpoint must also allow new cakes to be created.

Comments:
* We feel like the software stack used by the original developer is quite outdated, it would be good to migrate the entire application to something more modern.
* Would be good to change the application to implement proper client-server separation via REST API.

Bonus points:
* Tests
* Authentication via OAuth2
* Continuous Integration via any cloud CI system
* Containerisation


Original Project Info
=====================

To run a server locally execute the following command:

`mvn jetty:run`

and access the following URL:

`http://localhost:8282/`

Feel free to change how the project is run, but clear instructions must be given in README
You can use any IDE you like, so long as the project can build and run with Maven or Gradle.

The project loads some pre-defined data in to an in-memory database, which is acceptable for this exercise.  There is
no need to create persistent storage.


Submission
==========

Please provide your version of this project as a git repository (e.g. Github, BitBucket, etc).

Alternatively, you can submit the project as a zip or gzip. Use Google Drive or some other file sharing service to
share it with us.

Please also keep a log of the changes you make as a text file and provide this to us with your submission.

Good luck!
=================================================================================
