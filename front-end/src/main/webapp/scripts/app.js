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
  ]).factory('placesAvailable', ['$http', function($http) {
  var getData = function() {
    return $http.get("/posta/rest/places").then(function(response) {
      return response.data;
    });
  };
  return {
    getData: getData
  };
}])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {templateUrl: 'elements/home/home.html'})
      .when('/about', {templateUrl: 'elements/home/about.html'})
      .when('/employees', {templateUrl: 'elements/employees/employeesOverview.html', controller: 'EmployeesCtrl', controllerAs: 'employeesOverview'})
      .when('/jobs', {templateUrl: 'elements/jobs/jobsOverview.html', controller: 'JobsCtrl', controllerAs: 'jobsOverview'})
      .when('/jobsOfPlace', {templateUrl: 'elements/jobs/jobsOfPlace.html', controller: 'JobsOfPlaceCtrl', controllerAs: 'jobsOfPlace'})
      .when('/places', {templateUrl: 'elements/places/placesOverview.html', controller: 'PlacesCtrl', controllerAs: 'placesOverview'})
      .otherwise({redirectTo: '/'});
  });
