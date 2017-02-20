# Attendance Calculator
This is my own project for creating timetables for employees, where multi-shift work is active or workers work on irregular timetable basis.

### Correctness
I can not guarantee that all used frameworks and libraries are working and set up correctly. This whole project is only my experimental project, especially front-end (javascript) part.

# FAQ
### How can I run it?
![Build Status](https://travis-ci.org/piskula/AttendanceCalculator.svg?branch=master)

1. If you see green status here, you are able to simply clone this repository. If you see red, try to checkout [last green commit](https://github.com/piskula/AttendanceCalculator/commits/).
2. In project main directory run these commands:
  1. ``mvn clean install`` this downloads all dependencies and run unit tests
  2. ``cd front-end`` this changes directory
  3. ``mvn`` this command is configured to run tomcat - server
3. After that you have running server on your local machine and there are two ways how to communicate with application
  1. using your web browser (application is available on http://localhost:8080/posta/)
  2. using REST API (which your browser from previous case also uses, but data is in human-readable form that time). More information you can get in [REST API documentation](/front-end) (front-end README.md)
