'use strict';

angular.module('angularApp')
    .controller('EmployeesCtrl', ['$scope', '$http', '$location', 'commonTools', 'createUpdateTools', function ($scope, $http, $location, commonTools, createUpdateTools) {
        commonTools.getEmployeesAvailable().then(function (response) {
            $scope.employees = response;
        });

        $scope.titleWithComma = function(employee) {
            return employee.title == null ? "" : ", " + employee.title;
        }

        $scope.navigateToEdit = function (employee) {
            createUpdateTools.setItem(employee);
            $location.path('/createEmployee')
        };

        $scope.createEmployee = function () {
            $scope.navigateToEdit(null);
        };

        $scope.updateEmployee = function (employee) {
            $scope.navigateToEdit(employee);
        };
    }]);
