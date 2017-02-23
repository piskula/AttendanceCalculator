'use strict';

angular.module('angularApp')
    .controller('JobsOfDayCtrl', ['$scope', 'commonTools', '$http', function ($scope, commonTools, $http) {

        $scope.loaded = false;
        $scope.dayChoosen = new Date();
        $scope.reportType = 'place';

        $scope.changeGroupEmployeePlace = function () {
            if($scope.timeline != undefined) {
                $scope.timeline.destroy();
            }
            if($scope.reportType == 'place') {
                $scope.timeline = new vis.Timeline(document.getElementById("timeline"), new vis.DataSet($scope.placeData), $scope.placeGroups, $scope.options);
            } else {
                $scope.timeline = new vis.Timeline(document.getElementById("timeline"), new vis.DataSet($scope.employeeData), $scope.employeeGroups, $scope.options);
            }
            $scope.loaded = true;
        };

        $scope.groupOrder = function (a, b) {
            return a.value.localeCompare(b.value);
        }

        $scope.refreshData = function () {
            $scope.employeeData = [];
            $scope.placeData = [];
            var jobDate = $scope.dayChoosen;
            $scope.jobs.forEach(function (item, index) {
                var jobStart = item.jobStart.split(":");
                var jobEnd = item.jobEnd.split(":");
                $scope.employeeData.push({
                    id: item.id,
                    content: item.place.name,
                    title: commonTools.formatTimeRangeOfJob(item),
                    group: item.employee.id,
                    start: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobStart[0]).minutes(jobStart[1]),
                    end: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobEnd[0]).minutes(jobEnd[1]),
                    style: commonTools.getColors()[0]
                });
                $scope.placeData.push({
                    id: item.id,
                    content: commonTools.formatNameSurname(item.employee),
                    title: commonTools.formatTimeRangeOfJob(item),
                    group: item.place.id,
                    start: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobStart[0]).minutes(jobStart[1]),
                    end: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(jobEnd[0]).minutes(jobEnd[1]),
                    style: commonTools.getColors()[0]
                });
            });
            $scope.options = {
                min: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(5).minutes(0),
                start: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(5).minutes(0),
                max: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(20).minutes(0),
                end: moment().year(jobDate.getFullYear()).month(jobDate.getMonth()).date(jobDate.getDate()).hours(20).minutes(0),
                zoomable: false,
                moveable: false,
                orientation: 'top',
                stack: false,
                timeAxis: {scale: 'hour', step: 1},
                showMajorLabels: false,
                showMinorLabels: true,
                groupOrder: $scope.groupOrder
            };
            $scope.changeGroupEmployeePlace();
        };

        $scope.changeDateEvent = function () {
            $scope.loaded = false;
            $scope.dayString = commonTools.getDateFormatted($scope.dayChoosen);
            if($scope.dayChoosen != undefined) {
                $http({
                    url: '/posta/rest/jobs/findByCriteria',
                    method: "POST",
                    data: {
                        "jobDateStart": commonTools.formatDateForRest($scope.dayChoosen),
                    }
                }).then(function (response) {
                    $scope.jobs = response.data;
                    $scope.refreshData();
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

        $scope.initData = function () {
            $scope.employeeGroups = [];
            $scope.placeGroups = [];
            $scope.employeesAvailable.forEach(function (item, index) {
                $scope.employeeGroups.push({
                    id: item.id,
                    content: commonTools.formatNameSurname(item),
                    value: commonTools.formatSurnameName(item)
                });
            });
            $scope.placesAvailable.forEach(function (item, index) {
                $scope.placeGroups.push({
                    id: item.id,
                    content: item.name,
                    value: item.name
                });
            });
            $scope.changeDateEvent();
        };

        $scope.getPlacesAvailable = function () {
            commonTools.getPlacesAvailable().then(function (response) {
                $scope.placesAvailable = response;
                $scope.initData();
            })
        };
        commonTools.getEmployeesAvailable().then(function (response) {
            $scope.employeesAvailable = response;
            $scope.getPlacesAvailable();
        });

    }]);
