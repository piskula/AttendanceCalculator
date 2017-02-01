'use strict';

angular.module('angularApp')
    .controller('JobsOfPlaceCtrl', ['$scope', 'placesAvailable', '$http', function ($scope, placesAvailable, $http) {
        placesAvailable.getData().then(function (response) {
            $scope.places = response;
            $scope.selectedPlace = $scope.places[0];
            $scope.reloadJobs();
        });

        $scope.reloadJobs = function () {
            if($scope.selectedPlace.id != undefined) {
                $http({
                    url: '/posta/rest/jobs/findByCriteria',
                    method: "POST",
                    data: {
                        "placeId": $scope.selectedPlace.id
                    }
                }).then(function (response) {
                    $scope.jobs = response.data;
                    $scope.items = [[],[],[],[],[],[],[]];
                    $scope.employees = [];
                    $scope.nameGroups = [];
                    $scope.reloadDays();
                }, function (response) {
                    $scope.jobs = [];
                });
            }
            else {
                $scope.jobs = [];
            }
        };

        $scope.reloadDays = function() {
            $scope.jobs.forEach(function (item, index) {
                if($scope.employees.indexOf(item.employee.id) == -1) {
                    $scope.employees.push(item.employee.id);
                    $scope.nameGroups.push({
                        id: item.employee.id,
                        content: item.employee.name + ' ' + item.employee.surname
                    });
                }
                var d = new Date(item.jobDate);
                var jobStart = item.jobStart.split(":");
                var jobEnd = item.jobEnd.split(":");
                $scope.items[d.getDay() - 1].push({
                    id: item.id,
                    content: item.employee.name + ' ' + item.employee.surname,
                    group: item.employee.id,
                    start: moment().hours(jobStart[0]).minutes(jobStart[1]),
                    end: moment().hours(jobEnd[0]).minutes(jobEnd[1])
                });
            });

            $scope.timelineMonday.setGroups($scope.nameGroups);
            $scope.timelineMonday.setItems(new vis.DataSet($scope.items[0]));
            $scope.timelineTuesday.setGroups($scope.nameGroups);
            $scope.timelineTuesday.setItems(new vis.DataSet($scope.items[1]));
            $scope.timelineWednesday.setGroups($scope.nameGroups);
            $scope.timelineWednesday.setItems(new vis.DataSet($scope.items[2]));
            $scope.timelineThursday.setGroups($scope.nameGroups);
            $scope.timelineThursday.setItems(new vis.DataSet($scope.items[3]));
            $scope.timelineFriday.setGroups($scope.nameGroups);
            $scope.timelineFriday.setItems(new vis.DataSet($scope.items[4]));
            $scope.timelineSaturday.setGroups($scope.nameGroups);
            $scope.timelineSaturday.setItems(new vis.DataSet($scope.items[5]));
            $scope.timelineSunday.setGroups($scope.nameGroups);
            $scope.timelineSunday.setItems(new vis.DataSet($scope.items[6]));
        };

        // DOM element where the Timeline will be attached
        var containerHead = document.getElementById('vis0');
        var containerMonday = document.getElementById('vis1');
        var containerTuesday = document.getElementById('vis2');
        var containerWednesday = document.getElementById('vis3');
        var containerThursday = document.getElementById('vis4');
        var containerFriday = document.getElementById('vis5');
        var containerSaturday = document.getElementById('vis6');
        var containerSunday = document.getElementById('vis7');

        // Configuration for the Timeline
        var options = {
            min: moment().hours(5).minutes(0),
            start: moment().hours(5).minutes(0),
            max: moment().hours(20).minutes(0),
            end: moment().hours(20).minutes(0),
            zoomable: false,
            moveable: false,
            orientation: 'none',
            stack: false,
            timeAxis: {scale: 'hour', step: 1}
        };
        var optionsHead = {
            min: moment().hours(5).minutes(0),
            start: moment().hours(5).minutes(0),
            max: moment().hours(20).minutes(0),
            end: moment().hours(20).minutes(0),
            zoomable: false,
            moveable: false,
            orientation: 'top',
            stack: false,
            timeAxis: {scale: 'hour', step: 1}
        };

        // Create a Timeline
        $scope.timelineMonday = new vis.Timeline(containerMonday, new vis.DataSet([]), options);
        $scope.timelineTuesday = new vis.Timeline(containerTuesday, new vis.DataSet([]), options);
        $scope.timelineWednesday = new vis.Timeline(containerWednesday, new vis.DataSet([]), options);
        $scope.timelineThursday = new vis.Timeline(containerThursday, new vis.DataSet([]), options);
        $scope.timelineFriday = new vis.Timeline(containerFriday, new vis.DataSet([]), options);
        $scope.timelineSaturday = new vis.Timeline(containerSaturday, new vis.DataSet([]), options);
        $scope.timelineSunday = new vis.Timeline(containerSunday, new vis.DataSet([]), options);
        var timelineHead = new vis.Timeline(containerHead, new vis.DataSet([]), $scope.nameGroups, optionsHead);

    }]);
