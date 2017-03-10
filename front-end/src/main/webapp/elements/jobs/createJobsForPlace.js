'use strict';

angular.module('angularApp')
    .controller('CreateJobsForPlaceCtrl', ['$scope', '$http', 'commonTools', 'globalDate', function ($scope, $http, commonTools, globalDate) {

        $scope.loaded = false;
        $scope.dayChoosen = globalDate.get();
        
        $scope.changeDayEvent = function (indexDay) {
            $scope.dayTimeFrom[[indexDay]] = moment().hours($scope.timesFrom[[indexDay]][0]).minutes($scope.timesFrom[[indexDay]][1]).seconds(0).milliseconds(0);
            $scope.dayTimeTo[[indexDay]] = moment().hours($scope.timesTo[[indexDay]][0]).minutes($scope.timesTo[[indexDay]][1]).seconds(0).milliseconds(0);

            $scope.dayJobs[[indexDay]] = [[],[]];    //error messages + info messages
            $scope.jobsSorted[[indexDay]].forEach(function (item, index) {
                var jobStartSplit = item.jobStart.split(":");
                var jobEndSplit = item.jobEnd.split(":");
                var jobStart = moment().hours(jobStartSplit[0]).minutes(jobStartSplit[1]).seconds(0).milliseconds(0);
                var jobEnd = moment().hours(jobEndSplit[0]).minutes(jobEndSplit[1]).seconds(0).milliseconds(0);
                if ($scope.dayTimeFrom[[indexDay]].isAfter(jobStart) && $scope.dayTimeFrom[[indexDay]].isBefore(jobEnd)) {
                    $scope.dayJobs[[indexDay]][0].push("Start of this job "+ $scope.dayTimeFrom[[indexDay]].format("HH:mm") +" is in conflict with "+ commonTools.formatNameSurname(item.employee) +" "+ item.jobStart +" - "+ item.jobEnd);
                } else if ($scope.dayTimeTo[[indexDay]].isBefore(jobEnd) && $scope.dayTimeTo[[indexDay]].isAfter(jobStart)) {
                    $scope.dayJobs[[indexDay]][0].push("End of this job "+ $scope.dayTimeTo[[indexDay]].format("HH:mm") +" is in conflict with "+ commonTools.formatNameSurname(item.employee) +" "+ item.jobStart +" - "+ item.jobEnd);
                } else if ((!($scope.dayTimeFrom[[indexDay]].isAfter(jobStart))) && (!($scope.dayTimeTo[[indexDay]].isBefore(jobEnd)))) {
                    $scope.dayJobs[[indexDay]][0].push("Whole job "+ $scope.dayTimeFrom[[indexDay]].format("HH:mm") +" - "+ $scope.dayTimeTo[[indexDay]].format("HH:mm") +" is over "+ commonTools.formatNameSurname(item.employee) +" "+ item.jobStart +" - "+ item.jobEnd);
                } else if ($scope.selectedEmployee[[indexDay]].id == item.employee.id) {
                    var msg = commonTools.formatNameSurname(item.employee) +" already works here "+ item.jobStart +" - "+ item.jobEnd;
                    $scope.dayJobs[[indexDay]][1].push(msg);
                } else {
                    var msg = commonTools.formatNameSurname(item.employee) +" works also here "+ item.jobStart +" - "+ item.jobEnd;
                    $scope.dayJobs[[indexDay]][1].push(msg);
                }
            });
        };

        $scope.init = function() {
            $scope.selectedEmployee = [null,null,null,null,null,null];
            $scope.possibleMinutes = commonTools.getPossibleMinutes();
            $scope.possibleHours = commonTools.getPossibleHours();
            $scope.dayJobs = [[],[],[],[],[],[],[]];

            $scope.timesFrom = [];
            $scope.timesTo = [];
            $scope.dayTimeFrom = [];
            $scope.dayTimeTo = [];
            for (var i = 0; i < 7; i++) {
                $scope.timesFrom.push(['07', '00']);
                $scope.timesTo.push(['19', '00']);

                $scope.dayTimeFrom.push(moment().hours($scope.timesFrom[i][0]).minutes($scope.timesFrom[i][1]).seconds(0).milliseconds(0));
                $scope.dayTimeTo.push(moment().hours($scope.timesTo[i][0]).minutes($scope.timesTo[i][1]).seconds(0).milliseconds(0));
            }

            $scope.jobsSorted = [[],[],[],[],[],[],[]];
            $scope.allJobs.forEach(function (item, index) {
                var jobDate = new Date(item.jobDate);
                $scope.jobsSorted[jobDate.getDay() - 1].push(item);
            });

            $scope.loaded = true;
        };

        $scope.changeDate = function () {
            $scope.dayNumber = [];
            $scope.loaded = false;
            var d = new Date($scope.dayChoosen);
            globalDate.set(d);
            var tryParseMonday = new Date(d.setDate(d.getDate() - d.getDay() + (d.getDay() == 0 ? -6 : 1)));
            if(isNaN(tryParseMonday)) {
                $scope.dayString = "Incorrect week number!";
            } else {
                for (var i = 0; i < 7; i++) {
                    var day = new Date(tryParseMonday);
                    day.setDate(tryParseMonday.getDate() + i);
                    $scope.dayNumber.push(day.getDate());
                }
                $scope.saturday = new Date(tryParseMonday);
                $scope.saturday.setDate(tryParseMonday.getDate() + 5);
                $scope.dayString = commonTools.getShortDateRange(tryParseMonday, $scope.saturday);

                $http({
                    url: '/posta/rest/jobs/findByCriteria',
                    method: "POST",
                    data: {
                        "jobDateStart": commonTools.formatDateForRest(tryParseMonday),
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

        $scope.dayName = ['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'];
        $scope.getNumber = function(num) {
            return new Array(num);
        }
    }]);
