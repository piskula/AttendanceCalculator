'use strict';

angular.module('angularApp')
    .controller('CreateEmployeeCtrl', ['$scope', '$http', 'commonTools', 'createUpdateTools', function ($scope, $http, commonTools, createUpdateTools) {
        $scope.master = {};
        $scope.item = createUpdateTools.getItem();

        $scope.update = function(user) {
            $scope.status = "";
            $scope.master = angular.copy(user);
            if($scope.master.birth != undefined) {
                $scope.master.birth.setDate($scope.master.birth.getDate() + 1);
            }
            $http({
                url: '/posta/rest/employees',
                method: "POST",
                data: {
                    name: $scope.master.name,
                    surname: $scope.master.surname,
                    title: $scope.master.title,
                    birth: $scope.master.birth != null ? commonTools.formatDateForRest($scope.master.birth) : null,
                    email: $scope.master.email != undefined ? ($scope.master.email.length != 0 ? $scope.master.email : null) : null,
                    phone: $scope.master.phone != undefined ? ($scope.master.phone.length != 0 ? $scope.master.phone : null) : null
                }
            }).then(function (response) {
                $scope.status = response.status;
                $scope.reset();
            }, function (response) {
                $scope.status = response.status;
            });
        };

        // $scope.reset = function(form) {
        //     if (form) {
        //         form.$setPristine();
        //         form.$setUntouched();
        //     }
        //     $scope.user = angular.copy($scope.master);
        // };

        $scope.resetBirth = function (employee) {
            employee.birth = undefined;
        };

        // $scope.reset();
    }]);
