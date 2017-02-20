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
        }
    };
}])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {templateUrl: 'elements/home/home.html'})
      .when('/about', {templateUrl: 'elements/home/about.html'})
      .when('/employees', {templateUrl: 'elements/employees/employeesOverview.html', controller: 'EmployeesCtrl', controllerAs: 'employeesOverview'})
      .when('/jobs', {templateUrl: 'elements/jobs/jobsOverview.html', controller: 'JobsCtrl', controllerAs: 'jobsOverview'})
      .when('/jobsOfPlace', {templateUrl: 'elements/jobs/jobsOfPlace.html', controller: 'JobsOfPlaceCtrl', controllerAs: 'jobsOfPlace'})
      .when('/jobsOfEmployee', {templateUrl: 'elements/jobs/jobsOfEmployee.html', controller: 'JobsOfEmployeeCtrl', controllerAs: 'jobsOfEmployee'})
      .when('/places', {templateUrl: 'elements/places/placesOverview.html', controller: 'PlacesCtrl', controllerAs: 'placesOverview'})
      .otherwise({redirectTo: '/'});
  });
