'use strict';

angular.module('angularApp')
    .controller('PlacesCtrl', ['$scope','placesAvailable',function($scope, placesAvailable) {
        $scope.places = placesAvailable.getData().then(function (response) {
            $scope.places = response;
        })
    }]);
