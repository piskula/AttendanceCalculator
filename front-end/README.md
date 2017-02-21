# Documentation for REST API

## Employees

##### list all employees available
``curl -i -X GET http://localhost:8080/posta/rest/employees``

##### find specific employee by id
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

##### remove employee from system (if you know id)
``curl -i -X DELETE http://localhost:8080/posta/rest/employees/3``

##### update employee in system (if you know id)
``curl -X POST -i -H "Content-Type: application/json" --data '{"name":"Updated Name"}' http://localhost:8080/posta/rest/employees/update/4``
you are able to specify same fields (as when creating new employee)
