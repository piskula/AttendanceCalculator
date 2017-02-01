'use strict';

angular.module('angularApp')
    .controller('EmployeesCtrl', function ($scope, $http) {
        $http.get('/posta/rest/employees').then(function (response) {
            $scope.employees = response.data;
        }).then(function () {
            $scope.employees.sort(function (a, b) {
                if(a.surname == b.surname) {
                    if(a.name == b.name) {
                        return 0;
                    }
                    if(a.name < b.name) {
                        return -1;
                    }
                    return 1;
                }
                if(a.surname < b.surname) {
                    return -1;
                }
                return 1;
            })
        });

        $scope.titleWithComma = function(employee) {
            return employee.title == null ? "" : ", " + employee.title;
        }
    });
