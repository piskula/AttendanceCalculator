'use strict';

angular.module('angularApp')
    .controller('CreateEmployeeCtrl', ['$scope', '$http', '$location', 'commonTools', 'createUpdateTools', function ($scope, $http, $location, commonTools, createUpdateTools) {
        $scope.master = {};
        $scope.doing = 'create';
        if (createUpdateTools.getItem() != null) {
            $scope.employee = angular.copy(createUpdateTools.getItem());
            if(createUpdateTools.getItem().birth != undefined) {
                $scope.employee.birth = new Date(createUpdateTools.getItem().birth);
            }
            $scope.doing = 'update';
        }

        $scope.update = function(employee) {
            $scope.master = angular.copy(employee);
            if($scope.master.birth != undefined) {
                $scope.master.birth = new Date($scope.master.birth);
            }
            var data = {
                name: $scope.master.name,
                surname: $scope.master.surname,
                title: $scope.master.title != null ? ($scope.master.title.length != 0 ? $scope.master.title : null) : null,
                birth: $scope.master.birth != null ? commonTools.formatDateForRest($scope.master.birth) : null,
                email: $scope.master.email != undefined ? ($scope.master.email.length != 0 ? $scope.master.email : null) : null,
                phone: $scope.master.phone != undefined ? ($scope.master.phone.length != 0 ? $scope.master.phone : null) : null,
                address: $scope.master.address != undefined ? ($scope.master.address.length != 0 ? $scope.master.address : null) : null
            };
            if ($scope.doing == 'create') {
                $http({
                    url: '/posta/rest/employees',
                    method: "POST",
                    data: data
                }).then(function (response) {
                    $scope.status = "New employee successfully created.";
                    createUpdateTools.setAlerts([{type: 'success', title: 'Successfull!', msg: $scope.status}]);
                    createUpdateTools.deleteItem();
                    $location.path("/employees");
                }, function (response) {
                    $scope.status = "Cannot create, "+ response.status;
                });
            } else {
                $scope.messageBuilder = 'You have successfully updated these fields [';
                $scope.updatingItem = {};
                if (data.name != createUpdateTools.getItem().name) {
                    $scope.updatingItem.name = data.name;
                    $scope.messageBuilder += 'name, ';
                }
                if (data.surname != createUpdateTools.getItem().surname) {
                    $scope.updatingItem.surname = data.surname;
                    $scope.messageBuilder += 'surname, ';
                }
                if (!((data.title == null)
                    && (createUpdateTools.getItem().title == undefined || createUpdateTools.getItem().title == ""))) {
                    if (createUpdateTools.getItem().title != data.title) {
                        $scope.updatingItem.title = data.title;
                        $scope.messageBuilder += 'title, ';
                    }
                }
                if (!((data.birth == null)
                    && (createUpdateTools.getItem().birth == undefined || createUpdateTools.getItem().birth == ""))) {
                    var previousDate = (createUpdateTools.getItem().birth != undefined || createUpdateTools.getItem().birth != "") ? new Date(createUpdateTools.getItem().birth) : undefined;
                    if (previousDate.getTime() != $scope.master.birth.getTime()) {
                        $scope.updatingItem.birth = data.birth;
                        $scope.messageBuilder += 'birth, ';
                    }
                }
                if (!((data.email == null)
                    && (createUpdateTools.getItem().email == undefined || createUpdateTools.getItem().email == ""))) {
                    if (createUpdateTools.getItem().email != data.email) {
                        $scope.updatingItem.email = data.email;
                        $scope.messageBuilder += 'email, ';
                    }
                }
                if (!((data.phone == null)
                    && (createUpdateTools.getItem().phone == undefined || createUpdateTools.getItem().phone == ""))) {
                    if (createUpdateTools.getItem().phone != data.phone) {
                        $scope.updatingItem.phone = data.phone;
                        $scope.messageBuilder += 'phone, ';
                    }
                }
                if (!((data.address == null)
                    && (createUpdateTools.getItem().address == undefined || createUpdateTools.getItem().address == ""))) {
                    if (createUpdateTools.getItem().address != data.address) {
                        $scope.updatingItem.address = data.address;
                        $scope.messageBuilder += 'address, ';
                    }
                }
                if ($scope.messageBuilder.includes(', ')) {
                    $http({
                        url: '/posta/rest/employees/update/' + createUpdateTools.getItem().id,
                        method: "POST",
                        data: $scope.updatingItem
                    }).then(function (response) {
                        $scope.status = $scope.messageBuilder.substring(0, $scope.messageBuilder.length - 2) + "] of employee.";
                        createUpdateTools.setAlerts([{type: 'success', title:'Successfull!', msg: $scope.status}]);
                        createUpdateTools.deleteItem();
                        $location.path("/employees");
                    }, function (response) {
                        $scope.status = "Cannot update employee. An error "+ response.status +" occured.";
                    });
                } else {
                    createUpdateTools.setAlerts([{type: 'info', title: "No change!", msg: 'There have been no changes.'}]);
                    createUpdateTools.deleteItem();
                    $location.path("/employees");
                }
            }
        };

        $scope.reset = function(form) {
            if (form) {
                form.$setPristine();
                form.$setUntouched();
            }
            $scope.employee = null;
        };

        $scope.resetBirth = function (employee) {
            employee.birth = undefined;
        };


    }]);
