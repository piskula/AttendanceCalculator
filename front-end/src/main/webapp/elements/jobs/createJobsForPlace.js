'use strict';

angular.module('angularApp')
    .controller('CreateJobsForPlaceCtrl', ['$scope', '$http', 'commonTools', 'globalDate', function ($scope, $http, commonTools, globalDate) {

        $scope.loaded = false;
        $scope.dayChoosen = globalDate.get();
        
        $scope.changeEmployee1Event = function () {

        };

        $scope.init = function() {
            $scope.selectedEmployee = [null,null,null,null,null,null];
            $scope.possibleMinutes = commonTools.getPossibleMinutes();
            $scope.possibleHours = commonTools.getPossibleHours();

            $scope.timesFrom = [];
            $scope.timesTo = [];
            for (var i = 0; i < 7; i++) {
                $scope.timesFrom.push(['07', '00']);
                $scope.timesTo.push(['19', '00']);
            }

            $scope.jobsSorted = [[],[],[],[],[],[],[]];
            $scope.allJobs.forEach(function (item, index) {
                var jobDate = new Date(item.jobDate);
                $scope.jobsSorted[jobDate.getDay() - 1].push(item);
            });

            $scope.loaded = true;
        };

        $scope.changeDate = function () {
            $scope.loaded = false;
            var d = new Date($scope.dayChoosen);
            globalDate.set(d);
            var tryParseMonday = new Date(d.setDate(d.getDate() - d.getDay() + (d.getDay() == 0 ? -6 : 1)));
            if(isNaN(tryParseMonday)) {
                $scope.dayString = "Incorrect week number!";
            } else {
                $scope.monday = tryParseMonday;
                $scope.saturday = new Date($scope.monday);
                $scope.saturday.setDate($scope.monday.getDate() + 5);
                $scope.dayString = commonTools.getShortDateRange($scope.monday, $scope.saturday);

                $http({
                    url: '/posta/rest/jobs/findByCriteria',
                    method: "POST",
                    data: {
                        "jobDateStart": commonTools.formatDateForRest($scope.monday),
                        "jobDateEnd": commonTools.formatDateForRest($scope.saturday),
                        "placeId": $scope.selectedPlace.id
                    }
                }).then(function (response) {
                    $scope.allJobs = response.data;
                    $scope.init();
                }, function (response) {
                    $scope.allJobs = [];
                    $scope.loaded = true;
                });
            }
        };

        $scope.changePlace = function () {
            $scope.loaded = false;
            $scope.changeDate();
        };

        $scope.getPlacesAvailable = function () {
            commonTools.getPlacesAvailable().then(function (response) {
                $scope.placesAvailable = response;
                $scope.selectedPlace = $scope.placesAvailable[0];
                $scope.changePlace();
            })
        };
        commonTools.getEmployeesAvailable().then(function (response) {
            $scope.employeesAvailable = response;
            $scope.getPlacesAvailable();
        });

        // $scope.loaded = false;

        // $scope.dayDivs = [];
        // for (var i = 0; i < 7; i++) {
        //     $scope.dayDivs.push(document.getElementById('vis' + i));
        // }


    }]);
