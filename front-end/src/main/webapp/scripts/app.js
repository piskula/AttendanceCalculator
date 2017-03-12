'use strict';

/**
 * @ngdoc overview
 * @name angularApp
 * @description
 * # angularApp
 *
 * Main module of the application.
 */
angular
  .module('angularApp', [
    'ngRoute'
  ]).factory('commonTools', ['$http', function ($http) {
    return {
        getPlacesAvailable: function () {
            return $http.get("/posta/rest/places").then(function(response) {
                return response.data;
            });
        },
        getEmployeesAvailable: function () {
            return $http.get("/posta/rest/employees").then(function(response) {
                return response.data.sort(function (a, b) {
                    if(a.surname == b.surname) {
                        if(a.name == b.name) {
                            return 0;
                        }
                        if(a.name < b.name) {
                            return -1;
                        }
                        return 1;
                    }
                    if(a.surname < b.surname) {
                        return -1;
                    }
                    return 1;
                });
            });
        },
        formatDateForRest: function (dateToFormat) {
            return [dateToFormat.getFullYear(), (dateToFormat.getMonth() + 1), dateToFormat.getDate()];
        },
        formatNameSurname: function (employee) {
            return employee.name + " " + employee.surname;
        },
        formatSurnameName: function (employee) {
            return employee.surname + " " + employee.name;
        },
        formatTimeRangeOfJob: function (job) {
            return job.jobStart + " - " + job.jobEnd;
        },
        getColors: function () {
            return [
                "color: #000000; background-color: #ffc425;",
                "color: #000000; background-color: #83d064;",
                "color: #000000; background-color: #77aaff;",
                "color: #000000; background-color: #fb78c9;"
            ];
        },
        getShortDateRange: function (from, to) {
            return from.getDate() + "." + (from.getMonth()+1) + ". - " + to.getDate() + "." + (to.getMonth()+1) + ".";
        },
        getDateFormatted: function (date) {
            return date.getDate() + "." + (date.getMonth()+1) + "." + date.getFullYear();
        },
        getPossibleHours: function () {
            return ['05','06','07','08','09','10','11','12','13','14','15','16','17','18','19'];
        },
        getPossibleMinutes: function () {
            return ['00','05','10','15','20','25','30','35','40','45','50','55'];
        }
    };
}]).service('createUpdateTools', function () {
    var item = null;
    var alerts = [];
    return {
        getItem: function () {
            return item;
        },
        setItem: function (newItem) {
            item = newItem;
        },
        deleteItem: function () {
            item = null;
        },
        getAlerts: function () {
            return alerts;
        },
        addAlert: function (newAlert) {
            alerts.push(newAlert);
        },
        setAlerts: function (newAlerts) {
            alerts = newAlerts;
        },
        deleteAlerts: function () {
            alerts = [];
        }
    }
}).service('globalDate', function () {
    var globalDate = new Date();
    return {
        get: function () {
            return globalDate;
        },
        set: function (date) {
            globalDate = date;
        },
        delete: function () {
            globalDate = new Date();
        }
    }
})
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {templateUrl: 'elements/home/home.html'})
      .when('/about', {templateUrl: 'elements/home/about.html'})
      .when('/createEmployee', {templateUrl: 'elements/employees/createEmployee.html', controller: 'CreateEmployeeCtrl', controllerAs: 'createEmployee'})
      .when('/createJobsForPlace', {templateUrl: 'elements/jobs/createJobsForPlace.html', controller: 'CreateJobsForPlaceCtrl', controllerAs: 'createJobsForPlace'})
      .when('/createPlace', {templateUrl: 'elements/places/createPlace.html', controller: 'CreatePlaceCtrl', controllerAs: 'createPlace'})
      .when('/employees', {templateUrl: 'elements/employees/employeesOverview.html', controller: 'EmployeesCtrl', controllerAs: 'employeesOverview'})
      .when('/jobsOfDay', {templateUrl: 'elements/jobs/jobsOfDay.html', controller: 'JobsOfDayCtrl', controllerAs: 'jobsOfDay'})
      .when('/jobsOfEmployee', {templateUrl: 'elements/jobs/jobsOfEmployee.html', controller: 'JobsOfEmployeeCtrl', controllerAs: 'jobsOfEmployee'})
      .when('/jobsOfPlace', {templateUrl: 'elements/jobs/jobsOfPlace.html', controller: 'JobsOfPlaceCtrl', controllerAs: 'jobsOfPlace'})
      .when('/places', {templateUrl: 'elements/places/placesOverview.html', controller: 'PlacesCtrl', controllerAs: 'placesOverview'})
      .otherwise({redirectTo: '/'});
  });
