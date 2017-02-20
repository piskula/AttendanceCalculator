'use strict';

angular.module('angularApp')
    .controller('EmployeesCtrl', ['$scope', '$http', 'commonTools', function ($scope, $http, commonTools) {
        commonTools.getEmployeesAvailable().then(function (response) {
            $scope.employees = response;
        });

        $scope.titleWithComma = function(employee) {
            return employee.title == null ? "" : ", " + employee.title;
        }
    }]);
