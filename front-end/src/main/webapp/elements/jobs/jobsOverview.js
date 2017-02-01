'use strict';

angular.module('angularApp')
    .controller('JobsCtrl', function ($scope, $http) {
        $http({
            url: '/posta/rest/jobs/findByCriteria',
            method: "POST",
            data: {
                "employeeId":"1"
            }
        }).then(function (response) {
            $scope.jobs = response.data;
        });
    });
