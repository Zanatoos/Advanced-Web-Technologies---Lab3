# Advanced-Web-Technologies---Lab3

Introduction

Small application that launch a local web server programmed in JAVA.

Requirements

- Java SDK
- Maven

How to start the web server

- Open a terminal
- Use the cd command to be in the folder Lab3
- Then execute : `mvn  exec:java -Dexec.args="config.conf"` (will launch the web server using parameters of the config.conf file)

NOTE : If any change are made on the code, execute a `mvn -compile` before executing the previous command.

Use

Open a blank web page on your navigator, then go to http://localhost:8080, and you should see the html file named index.html in the www folder.
