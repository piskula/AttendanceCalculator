'use strict';

angular.module('angularApp')
    .controller('PlacesCtrl', ['$scope', '$http', '$location', 'commonTools', 'createUpdateTools', function ($scope, $http, $location, commonTools, createUpdateTools) {
        commonTools.getPlacesAvailable().then(function (response) {
            $scope.places = response;
        });

        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        };


        $scope.navigateToEdit = function (place) {
            createUpdateTools.setItem(place);
            $location.path('/createPlace')
        };

        $scope.createPlace = function () {
            $scope.navigateToEdit(null);
        };

        $scope.updatePlace = function (place) {
            $scope.navigateToEdit(place);
        };
    }]);
