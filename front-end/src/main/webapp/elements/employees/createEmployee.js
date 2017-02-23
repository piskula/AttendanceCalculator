'use strict';

angular.module('angularApp')
    .controller('CreateEmployeeCtrl', ['$scope', '$http', 'commonTools', function ($scope, $http, commonTools) {
        $scope.master = {};

        $scope.update = function(user) {
            $scope.status = "";
            $scope.master = angular.copy(user);
            $http({
                url: '/posta/rest/employees',
                method: "POST",
                data: $scope.master
            }).then(function (response) {
                $scope.status = response.status;
                $scope.reset();
            }, function (response) {
                $scope.status = "Error";
            });
        };

        $scope.reset = function(form) {
            if (form) {
                form.$setPristine();
                form.$setUntouched();
            }
            $scope.user = angular.copy($scope.master);
        };

        $scope.reset();
    }]);
