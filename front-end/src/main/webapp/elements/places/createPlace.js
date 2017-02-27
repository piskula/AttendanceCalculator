'use strict';

angular.module('angularApp')
    .controller('CreatePlaceCtrl', ['$scope', '$http', 'commonTools', function ($scope, $http, commonTools) {
        $scope.master = {};

        $scope.update = function(place) {
            $scope.status = "";
            $scope.master = angular.copy(place);
            $http({
                url: '/posta/rest/places',
                method: "POST",
                data: $scope.master
            }).then(function (response) {
                $scope.status = response.status;
            }, function (response) {
                $scope.status = response.status;
            });
        };

        $scope.resetBirth = function (user) {
            user.birth = undefined;
        };

    }]);
