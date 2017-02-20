'use strict';

angular.module('angularApp')
    .controller('JobsOfPlaceCtrl', ['$scope', '$http', 'commonTools', function ($scope, $http, commonTools) {
        //load all places into <select>
        commonTools.getPlacesAvailable().then(function (response) {
            $scope.loaded = false;
            $scope.places = response;
            $scope.selectedPlace = $scope.places[0];
            $scope.init();
        });

        $scope.loaded = false;
        $scope.dayChoosen = new Date();
        $scope.nameColors = [
            "color: #000000; background-color: #ffc425;",
            "color: #000000; background-color: #83d064;",
            "color: #000000; background-color: #77aaff;",
            "color: #000000; background-color: #fb78c9;"
        ];
        $scope.dayDivs = [];
        for (var i = 0; i < 7; i++) {
            $scope.dayDivs.push(document.getElementById('vis' + i));
        }

        $scope.refreshDays = function() {
            $scope.items = [[],[],[],[],[],[],[]];
            $scope.options = [];
            $scope.allEmployeeIDs = [];
            $scope.employeeIDs = [[],[],[],[],[],[],[]];
            $scope.nameGroups = [[],[],[],[],[],[],[]];

            $scope.jobs.forEach(function (item, index) {
                if ($scope.allEmployeeIDs.indexOf(item.employee.id) == -1) {
                    $scope.allEmployeeIDs.push(item.employee.id);
                }
            });
            $scope.jobs.forEach(function (item, index) {
                var jobDate = new Date(item.jobDate);
                if ($scope.employeeIDs[jobDate.getDay() - 1].indexOf(item.employee.id) == -1) {
                    $scope.employeeIDs[jobDate.getDay() - 1].push(item.employee.id);
                    $scope.nameGroups[jobDate.getDay() - 1].push({
                        id: item.employee.id,
                        content: item.employee.name + ' ' + item.employee.surname
                    });
                }
                var jobStart = item.jobStart.split(":");
                var jobEnd = item.jobEnd.split(":");
                $scope.items[jobDate.getDay() - 1].push({
                    id: item.id,
                    content: item.employee.name + ' ' + item.employee.surname,
                    title: item.jobStart + " - " + item.jobEnd,
                    group: item.employee.id,
                    start: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobStart[0]).minutes(jobStart[1]),
                    end: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobEnd[0]).minutes(jobEnd[1]),
                    style: $scope.nameColors[$scope.allEmployeeIDs.indexOf(item.employee.id) % $scope.nameColors.length]
                });
            });

            for (var i = 0; i < 7; i++) {
                $scope.options.push({
                    min: moment().year(2017).month(0).date(2).hours(5).minutes(0).add(i, 'days'),
                    start: moment().year(2017).month(0).date(2).hours(5).minutes(0).add(i, 'days'),
                    max: moment().year(2017).month(0).date(2).hours(20).minutes(0).add(i, 'days'),
                    end: moment().year(2017).month(0).date(2).hours(20).minutes(0).add(i, 'days'),
                    zoomable: false,
                    moveable: false,
                    orientation: 'top',
                    stack: false,
                    timeAxis: {scale: 'hour', step: 1},
                    showMajorLabels: false,
                    showMinorLabels: true
                });
            };

            if($scope.timelineMonday != undefined) { $scope.timelineMonday.destroy(); }
            $scope.timelineMonday = new vis.Timeline($scope.dayDivs[0], new vis.DataSet($scope.items[0]), $scope.nameGroups[0], $scope.options[0]);

            if($scope.timelineTuesday != undefined) { $scope.timelineTuesday.destroy(); }
            $scope.timelineTuesday = new vis.Timeline($scope.dayDivs[1], new vis.DataSet($scope.items[1]), $scope.nameGroups[1], $scope.options[1]);

            if($scope.timelineWednesday != undefined) { $scope.timelineWednesday.destroy(); }
            $scope.timelineWednesday = new vis.Timeline($scope.dayDivs[2], new vis.DataSet($scope.items[2]), $scope.nameGroups[2], $scope.options[2]);

            if($scope.timelineThursday != undefined) { $scope.timelineThursday.destroy(); }
            $scope.timelineThursday = new vis.Timeline($scope.dayDivs[3], new vis.DataSet($scope.items[3]), $scope.nameGroups[3], $scope.options[3]);

            if($scope.timelineFriday != undefined) { $scope.timelineFriday.destroy(); }
            $scope.timelineFriday = new vis.Timeline($scope.dayDivs[4], new vis.DataSet($scope.items[4]), $scope.nameGroups[4], $scope.options[4]);

            if($scope.timelineSaturday != undefined) { $scope.timelineSaturday.destroy(); }
            $scope.timelineSaturday = new vis.Timeline($scope.dayDivs[5], new vis.DataSet($scope.items[5]), $scope.nameGroups[5], $scope.options[5]);

            if($scope.timelineSunday != undefined) { $scope.timelineSunday.destroy(); }
            $scope.timelineSunday = new vis.Timeline($scope.dayDivs[6], new vis.DataSet($scope.items[6]), $scope.nameGroups[6], $scope.options[6]);

            $scope.loaded = true;
        };

        $scope.reloadJobs = function () {
            $scope.loaded = false;
            if($scope.selectedPlace.id != undefined) {
                $http({
                    url: '/posta/rest/jobs/findByCriteria',
                    method: "POST",
                    data: {
                        "placeId": $scope.selectedPlace.id,
                        "jobDateStart": commonTools.formatDateForRest($scope.monday),
                        "jobDateEnd": commonTools.formatDateForRest($scope.saturday)
                    }
                }).then(function (response) {
                    $scope.jobs = response.data;
                    $scope.refreshDays();
                }, function (response) {
                    $scope.jobs = [];
                    $scope.loaded = true;
                });
            }
            else {
                $scope.jobs = [];
            }
        };

        $scope.changePlaceEvent = function () {
            $scope.reloadJobs();
        }

        $scope.changeDateEvent = function() {
            var d = new Date($scope.dayChoosen);
            var tryParseMonday = new Date(d.setDate(d.getDate() - d.getDay() + (d.getDay() == 0 ? -6 : 1)));
            if(isNaN(tryParseMonday)) {
                $scope.dayString = "Incorrect week number!";
            } else {
                $scope.monday = tryParseMonday;
                $scope.saturday = new Date($scope.monday);
                $scope.saturday.setDate($scope.monday.getDate() + 5);
                $scope.dayString = $scope.monday.getDate() + "." + ($scope.monday.getMonth()+1)
                    + ". - " + $scope.saturday.getDate() + "." + ($scope.saturday.getMonth()+1) + ".";
                $scope.changePlaceEvent();
            }
        };

        $scope.init = function() {
            $scope.changeDateEvent();
        };

    }]);
