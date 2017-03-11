# Documentation for REST API

## Employees

##### list all employees available
``curl -i -X GET http://localhost:8080/posta/rest/employees``

##### find specific employee by ID
``curl -i -X GET http://localhost:8080/posta/rest/employees/findId/3``

##### find employees by key (name, surname, both)
``curl -X GET -i -H "Content-Type: application/json" --data '{"name":"Mark Webber"}' http://localhost:8080/posta/rest/employees/find``

##### create new employee in system
``curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Michael","surname":"Schumacher","birth":"1971-12-06"}' http://localhost:8080/posta/rest/employees``
you are able to specify these fields:
* name ``"name":"Sebastian"`` [required]
* surname ``"surname":"Vettel"`` [required]
* title ``"title":"Mr."``
* birth ``"birth":"1987-07-03"``
* phone ``"phone":"+420 568 985 642"``
* address ``"address":"Bergstrase, Heppenheim, Germany"``
* email ``"email":"seb@forzaferrari.it"``
* annotation ``"annotation":"4 time world champion"``

##### remove employee from system (if you know ID)
``curl -i -X DELETE http://localhost:8080/posta/rest/employees/3``

##### update employee in system (if you know ID)
``curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Updated Name"}' http://localhost:8080/posta/rest/employees/update/4``
you are able to specify same fields (as when creating new employee)

## Places

##### list all places
``curl -i -X GET http://localhost:8080/posta/rest/places``

##### find specific place by ID
``curl -i -X GET http://localhost:8080/posta/rest/places/findId/3``

##### find specific place by name
``curl -X GET -i -H "Content-Type: application/json" --data '{"name":"Priehradka c.2"}' http://localhost:8080/posta/rest/places/findName``

##### create new place
``curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Priehradka c.22","placeType":"WINDOW","annotation":"priehradka 22"}' http://localhost:8080/posta/rest/places``
you are able to specify these fields:
* name ``"name":"Window n.1"`` [required]
* type ``"type":"WINDOW"`` [required]
* annotation ``"annotation":"special window"``

##### remove specific place (if you know ID)
``curl -i -X DELETE http://localhost:8080/posta/rest/places/3``

##### update specific place (if you know ID)
``curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Window n.4 new"}' http://localhost:8080/posta/rest/places/update/4``
you are able to specify same fields (as when creating new place)

## Jobs

##### list all jobs
``curl -i -X GET http://localhost:8080/posta/rest/jobs``
this method is only for developing purposes,
DO NOT use it when you have a lot of data stored in DB,
instead of it, please, use one of *find* methods

##### find specific job
``curl -i -X GET http://localhost:8080/posta/rest/jobs/findId/3``

##### create new job in system (if you know specific place ID and employee ID)
``curl -X POST -i -H "Content-Type: application/json" --data '{"employeeId":"3","placeId":"2","jobDate":"2017-01-20","jobStart":[14,15],"jobEnd":[14,25]}' http://localhost:8080/posta/rest/jobs``
you are able to specify these fields:
* employeeId ``"employeeId":"3"`` [required]
* type ``"placeId":"2"`` [required]
* jobDate ``"jobDate":"2017-01-20"`` [required]
* jobStart ``"jobStart":[14,15]`` [required]
* jobEnd ``"jobEnd":[14,25]`` [required]

##### remove job (if you know ID)
``curl -i -X DELETE http://localhost:8080/posta/rest/jobs/3``

##### update job (if you know ID)
``curl -X POST -i -H "Content-Type: application/json" --data '{"employeeId":"2","jobStart":[6,10]}' http://localhost:8080/posta/rest/jobs/update/3``
you are able to specify same fields (as when creating new job)

##### find specific jobs by criteria
``curl -X POST -i -H "Content-Type: application/json" --data '{"employeeId":"1"}' http://localhost:8080/posta/rest/jobs/findByCriteria``
This methods allows you to find jobs by specifying one or more details:
 1. Employee - find all jobs of specified employee
 2. Place - find all jobs according to specified place
 3. Date - find all jobs by specifying one exact day
 4. Employee & date/dates
 5. Place & date/dates
