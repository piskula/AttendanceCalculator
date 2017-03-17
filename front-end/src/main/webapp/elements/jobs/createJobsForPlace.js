'use strict';

angular.module('angularApp')
    .controller('CreateJobsForPlaceCtrl', ['$scope', '$http', '$location', '$q', 'commonTools', 'globalDate', 'createUpdateTools', function ($scope, $http, $location, $q, commonTools, globalDate, createUpdateTools) {

        $scope.loaded = false;
        $scope.dayChoosen = globalDate.get();

        $scope.createOneJob = function () {
            if($scope.statusesOk.length != 0) {
                $http($scope.httpJobs[0]
                ).then(function (response) {
                    $scope.httpJobs.splice(0, 1);
                    createUpdateTools.addAlert({type: 'success', title: 'Successful!', msg: $scope.statusesOk[0]});
                    $scope.statusesOk.splice(0, 1);
                    $scope.statusesError.splice(0, 1);
                    $scope.createOneJob();
                }, function (response) {
                    $scope.httpJobs.splice(0, 1);
                    createUpdateTools.addAlert({type: 'danger', title: 'Error!', msg: $scope.statusesError[0] + response.status});
                    $scope.statusesOk.splice(0, 1);
                    $scope.statusesError.splice(0, 1);
                    $scope.createOneJob();
                });
            } else {
                createUpdateTools.setItem($scope.selectedPlace.id);
                $location.path("/jobsOfPlace");
            }
        };

        $scope.createJobs = function () {
            createUpdateTools.deleteAlerts();
            var d = new Date($scope.dayChoosen);
            var tryParseMonday = new Date(d.setDate(d.getDate() - d.getDay() + (d.getDay() == 0 ? -6 : 1)));
            $scope.httpJobs = [];
            $scope.statusesOk = [];
            $scope.statusesError = [];
            $scope.selectedEmployee.forEach(function (item, index) {
                if (item != null) {
                    var myDay = new Date(tryParseMonday);
                    myDay.setDate(tryParseMonday.getDate() + index);
                    var data = {
                        jobStart: [parseInt($scope.timesFrom[index][0]),parseInt($scope.timesFrom[index][1])],
                        jobEnd: [parseInt($scope.timesTo[index][0]),parseInt($scope.timesTo[index][1])],
                        jobDate: commonTools.formatDateForRest(myDay),
                        employeeId: item.id,
                        placeId: $scope.selectedPlace.id
                    };

                    $scope.httpJobs.push({
                        url: '/posta/rest/jobs',
                        method: "POST",
                        data: data
                    });

                    var statusOk = "New job "+  $scope.dayTimeFrom[[index]].format("HH:mm") +" - "+ $scope.dayTimeTo[[index]].format("HH:mm") +" "+ commonTools.getDateFormatted(myDay) +" for "+ commonTools.formatNameSurname(item) +" successfully created.";
                    var statusError = "Job "+ $scope.dayTimeFrom[[index]].format("HH:mm") +" - "+ $scope.dayTimeTo[[index]].format("HH:mm") +" "+ commonTools.getDateFormatted(myDay)  +" for "+ commonTools.formatNameSurname(item) +" has not been created! HTTP ";
                    $scope.statusesOk.push(statusOk);
                    $scope.statusesError.push(statusError);
                }
            });

            if($scope.statusesOk.length != 0) {
                $scope.createOneJob();
            } else {
                createUpdateTools.setItem($scope.selectedPlace.id);
                createUpdateTools.addAlert({type: 'info', title: 'No change!', msg: "You have not created any job."});
                $location.path("/jobsOfPlace");
            }

        };

        $scope.changeDayEvent = function (indexDay) {
            $scope.dayTimeFrom[indexDay] = moment().hours($scope.timesFrom[indexDay][0]).minutes($scope.timesFrom[indexDay][1]).seconds(0).milliseconds(0);
            $scope.dayTimeTo[indexDay] = moment().hours($scope.timesTo[indexDay][0]).minutes($scope.timesTo[indexDay][1]).seconds(0).milliseconds(0);
            if ($scope.dayTimeFrom[indexDay].isAfter($scope.dayTimeTo[indexDay])) {
                $scope.timeOk[indexDay] = false;
            } else {
                $scope.timeOk[indexDay] = true;
            }

            $scope.dayJobs[indexDay] = [[],[]];    //error messages + info messages
            $scope.jobsSorted[indexDay].forEach(function (item, index) {
                var jobStartSplit = item.jobStart.split(":");
                var jobEndSplit = item.jobEnd.split(":");
                var jobStart = moment().hours(jobStartSplit[0]).minutes(jobStartSplit[1]).seconds(0).milliseconds(0);
                var jobEnd = moment().hours(jobEndSplit[0]).minutes(jobEndSplit[1]).seconds(0).milliseconds(0);
                if ($scope.selectedEmployee[indexDay].id == item.employee.id) {
                    if ($scope.dayTimeFrom[indexDay].isAfter(jobStart) && $scope.dayTimeFrom[indexDay].isBefore(jobEnd)) {
                        $scope.dayJobs[indexDay][0].push({what: "Start of this job ", when: $scope.dayTimeFrom[indexDay].format("HH:mm"), why: " is in conflict with ", whom: commonTools.formatNameSurname(item.employee) +" "+ item.jobStart +" - "+ item.jobEnd});
                    } else if ($scope.dayTimeTo[indexDay].isBefore(jobEnd) && $scope.dayTimeTo[indexDay].isAfter(jobStart)) {
                        $scope.dayJobs[indexDay][0].push({what: "End of this job ", when: $scope.dayTimeTo[indexDay].format("HH:mm"), why: " is in conflict with ", whom: commonTools.formatNameSurname(item.employee) +" "+ item.jobStart +" - "+ item.jobEnd});
                    } else if ((!($scope.dayTimeFrom[indexDay].isAfter(jobStart))) && (!($scope.dayTimeTo[indexDay].isBefore(jobEnd)))) {
                        $scope.dayJobs[indexDay][0].push({what: "Whole job ", when: $scope.dayTimeFrom[indexDay].format("HH:mm") +" - "+ $scope.dayTimeTo[indexDay].format("HH:mm"), why: " is over "+ commonTools.formatNameSurname(item.employee) +" ", whom: item.jobStart +" - "+ item.jobEnd +" "+ item.place.name});
                    } else {
                        $scope.dayJobs[indexDay][1].push({who: commonTools.formatNameSurname(item.employee), msg: " works also at ", place: item.place.name + " ", when: item.jobStart + " - " + item.jobEnd});
                    }
                } else {
                    if ($scope.selectedPlace.id == item.place.id) {
                        if ($scope.dayTimeFrom[indexDay].isAfter(jobStart) && $scope.dayTimeFrom[indexDay].isBefore(jobEnd)
                            || $scope.dayTimeTo[indexDay].isBefore(jobEnd) && $scope.dayTimeTo[indexDay].isAfter(jobStart)
                            || (!($scope.dayTimeFrom[indexDay].isAfter(jobStart))) && (!($scope.dayTimeTo[indexDay].isBefore(jobEnd)))) {
                            $scope.dayJobs[indexDay][0].push({what: "", when: commonTools.formatNameSurname(item.employee), why: " already works at ", whom: item.place.name +" "+ item.jobStart +" - "+ item.jobEnd});
                        } else {
                            $scope.dayJobs[indexDay][1].push({who: commonTools.formatNameSurname(item.employee), msg: " works also at ", place: item.place.name + " ", when: item.jobStart +" - "+ item.jobEnd});
                        }
                    }
                }
            });
        };

        $scope.init = function() {
            $scope.selectedEmployee = [null,null,null,null,null,null];
            $scope.possibleMinutes = commonTools.getPossibleMinutes();
            $scope.possibleHours = commonTools.getPossibleHours();
            $scope.dayJobs = [[],[],[],[],[],[],[]];
            $scope.timeOk = [true,true,true,true,true,true,true];

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
                        "jobDateEnd": commonTools.formatDateForRest($scope.saturday)
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
