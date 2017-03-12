'use strict';

angular.module('angularApp')
    .controller('JobsOfEmployeeCtrl', ['$scope', 'commonTools', '$http', 'globalDate', function ($scope, commonTools, $http, globalDate) {
        //load all employees into <select>
        commonTools.getEmployeesAvailable().then(function (response) {
            $scope.loaded = false;
            $scope.employees = response;
            $scope.selectedEmployee = $scope.employees[0];
            $scope.init();
        });

        $scope.loaded = false;
        $scope.dayChoosen = globalDate.get();
        $scope.dayDivs = [];
        for (var i = 0; i < 7; i++) {
            $scope.dayDivs.push(document.getElementById('vis' + i));
        }

        $scope.refreshDays = function() {
            $scope.items = [[],[],[],[],[],[],[]];
            $scope.options = [];
            $scope.allPlaceIDs = [];
            $scope.placeIDs = [[],[],[],[],[],[],[]];
            $scope.nameGroups = [[],[],[],[],[],[],[]];

            $scope.jobs.forEach(function (item, index) {
                if ($scope.allPlaceIDs.indexOf(item.place.id) == -1) {
                    $scope.allPlaceIDs.push(item.place.id);
                }
            });
            $scope.jobs.forEach(function (item, index) {
                var jobDate = new Date(item.jobDate);
                if ($scope.placeIDs[jobDate.getDay() - 1].indexOf(item.place.id) == -1) {
                    $scope.placeIDs[jobDate.getDay() - 1].push(item.place.id);
                    $scope.nameGroups[jobDate.getDay() - 1].push({
                        id: item.place.id,
                        content: item.place.name
                    });
                }
                var jobStart = item.jobStart.split(":");
                var jobEnd = item.jobEnd.split(":");
                $scope.items[jobDate.getDay() - 1].push({
                    id: item.id,
                    content: item.place.name,
                    title: commonTools.formatTimeRangeOfJob(item) +" "+ item.place.name,
                    group: item.place.id,
                    start: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobStart[0]).minutes(jobStart[1]),
                    end: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobEnd[0]).minutes(jobEnd[1]),
                    style: commonTools.getColors()[$scope.allPlaceIDs.indexOf(item.place.id) % commonTools.getColors().length]
                });
            });

            for (var i = 0; i < 7; i++) {
                var startingPoint = moment($scope.monday).hours(5).minutes(0).add(i, 'days');
                var endingPoint = moment($scope.monday).hours(20).minutes(0).add(i, 'days');
                $scope.options.push({
                    min: startingPoint,
                    start: startingPoint,
                    max: endingPoint,
                    end: endingPoint,
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
            if($scope.selectedEmployee.id != undefined) {
                $http({
                    url: '/posta/rest/jobs/findByCriteria',
                    method: "POST",
                    data: {
                        "employeeId": $scope.selectedEmployee.id,
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
                $scope.loaded = true;
            }
        };

        $scope.changeEmployeeEvent = function () {
            $scope.reloadJobs();
        }

        $scope.changeDateEvent = function() {
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
                $scope.changeEmployeeEvent();
            }
        };

        $scope.init = function() {
            $scope.changeDateEvent();
        };

        $scope.getEmployeeName = function () {
            if($scope.selectedEmployee != undefined) {
                return $scope.selectedEmployee.name + " " + $scope.selectedEmployee.surname;
            }
            return "";
        }

    }]);
