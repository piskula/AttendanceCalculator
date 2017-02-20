'use strict';

angular.module('angularApp')
    .controller('PlacesCtrl', ['$scope','commonTools',function($scope, commonTools) {
        commonTools.getPlacesAvailable().then(function (response) {
            $scope.places = response;
        });
    }]);
