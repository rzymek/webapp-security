# Simple file storing and sharing service.

You can upload your files and optionally publish some of them with a rich text description. 

## Start server:

    mvn spring-boot:run

Open http://localhost:8080/

Demo login:

    User: admin
    Pass: admin

## Functionality

1. Login to access your files collection
2. Upload file
3. Delete file
4. Download
5. Filter your files by name
6. Publish file with rich-text description
7. Logout
8. Download published file

## Source code

Spring Boot Web Application. 

* Templating engine: JSP. 
* Build tool: maven.
* Database: embedded H2

Source walk-through:

* `src/main/java/` - Java sources
    * `pl.lingaro.od.workshop.security.config.*` - Spring Security config
    * `pl.lingaro.od.workshop.security.data.*` - JPA entities
    * `pl.lingaro.od.workshop.security.AppController` - one and only web controller
    * `pl.lingaro.od.workshop.security.Application` - application's `main()` method 
* `src/main/resources/application.properties` - Spring Boot config
* `src/main/resources/data.sql` - initial database values
* `src/main/webapp/WEB-INF/tags/template.tag` - pages template
* `src/main/webapp/WEB-INF/jsp` - site pages. Using the template and values supplied by AppController via `Model`.

## HTTPS certificate

    keytool -genkey -alias tomcat -storetype PKCS12   \
      -keyalg RSA -keysize 2048 -keystore keystore.p12\
      -validity 3650