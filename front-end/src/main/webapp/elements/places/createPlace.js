'use strict';

angular.module('angularApp')
    .controller('CreatePlaceCtrl', ['$scope', '$http', '$location', 'commonTools', 'createUpdateTools', function ($scope, $http, $location, commonTools, createUpdateTools) {
        $scope.master = {};
        $scope.doing = 'create';
        if (createUpdateTools.getItem() != null) {
            $scope.place = angular.copy(createUpdateTools.getItem());
            createUpdateTools.deleteItem();
            $scope.genuinePlace = angular.copy($scope.place);
            $scope.doing = 'update';
        }

        $scope.update = function(place) {
            $scope.master = angular.copy(place);
            var data = {
                name: $scope.master.name,
                placeType: $scope.master.placeType,
            };
            if ($scope.doing == 'create') {
                $http({
                    url: '/posta/rest/places',
                    method: "POST",
                    data: data
                }).then(function (response) {
                    $scope.status = "New place successfully created.";
                    createUpdateTools.setAlerts([{type: 'success', title: 'Successfull!', msg: $scope.status}]);
                    $location.path("/places");
                }, function (response) {
                    $scope.status = "Cannot create, "+ response.status;
                });
            } else {
                $scope.messageBuilder = 'You have successfully updated these fields [';
                $scope.updatingItem = {};
                if (data.name != $scope.genuinePlace.name) {
                    $scope.updatingItem.name = data.name;
                    $scope.messageBuilder += 'name, ';
                }
                if (data.placeType != $scope.genuinePlace.placeType) {
                    $scope.updatingItem.placeType = data.placeType;
                    $scope.messageBuilder += 'placeType, ';
                }
                if ($scope.messageBuilder.includes(', ')) {
                    $http({
                        url: '/posta/rest/places/update/' + $scope.genuinePlace.id,
                        method: "POST",
                        data: $scope.updatingItem
                    }).then(function (response) {
                        $scope.status = $scope.messageBuilder.substring(0, $scope.messageBuilder.length - 2) + "] of place.";
                        createUpdateTools.setAlerts([{type: 'success', title:'Successfull!', msg: $scope.status}]);
                        $location.path("/places");
                    }, function (response) {
                        $scope.status = "Cannot update place. An error "+ response.status +" occured.";
                    });
                } else {
                    createUpdateTools.setAlerts([{type: 'info', title: "No change!", msg: 'There have been no changes.'}]);
                    $location.path("/places");
                }
            }
        };

        $scope.reset = function(form) {
            if (form) {
                form.$setPristine();
                form.$setUntouched();
            }
            $scope.place = null;
        };

        $scope.removePlace = function () {
            if($scope.genuinePlace == null || $scope.genuinePlace.id == undefined) {
                confirm('Cannot remove Place immediately.\nFor removing place we need to know unique identification, which is not available now.');
                $location.path("/places");
            } else {
                if(confirm('You are above to completely remove Place:\n'+ $scope.genuinePlace.name +'\n\nAre you sure?')) {
                    $http({
                        url: '/posta/rest/places/' + $scope.genuinePlace.id,
                        method: "DELETE"
                    }).then(function () {
                        createUpdateTools.setAlerts([{type: 'success', title:'Removed!', msg: 'You have successfully deleted '+ $scope.genuinePlace.name +' from system.'}]);
                        $location.path("/places");
                    }, function () {
                        createUpdateTools.setAlerts([{type: 'danger', title:'Cannot Remove!', msg: $scope.genuinePlace.name +' is assigned to some jobs.'}]);
                        $location.path("/places");
                    })
                }
            }
        };

    }]);
