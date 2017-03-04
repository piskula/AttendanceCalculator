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
            $scope.genuineEmployee = angular.copy($scope.employee);
            createUpdateTools.deleteItem();
            $scope.doing = 'update';
        }

        $scope.update = function(employee) {
            $scope.master = angular.copy(employee);
            if($scope.master.birth != undefined) {
                $scope.master.birth = new Date($scope.master.birth);
            }

            if ($scope.doing == 'create') {
                var data = {
                    name: $scope.master.name,
                    surname: $scope.master.surname,
                    title: $scope.master.title != null ? ($scope.master.title.length != 0 ? $scope.master.title : null) : null,
                    birth: $scope.master.birth != null ? commonTools.formatDateForRest($scope.master.birth) : null,
                    email: $scope.master.email != undefined ? ($scope.master.email.length != 0 ? $scope.master.email : null) : null,
                    phone: $scope.master.phone != undefined ? ($scope.master.phone.length != 0 ? $scope.master.phone : null) : null,
                    address: $scope.master.address != undefined ? ($scope.master.address.length != 0 ? $scope.master.address : null) : null
                };
                $http({
                    url: '/posta/rest/employees',
                    method: "POST",
                    data: data
                }).then(function (response) {
                    $scope.status = "New employee successfully created.";
                    createUpdateTools.setAlerts([{type: 'success', title: 'Successfull!', msg: $scope.status}]);
                    $location.path("/employees");
                }, function (response) {
                    $scope.status = "Cannot create, "+ response.status;
                });
            } else {
                var data = {
                    name: $scope.master.name,
                    surname: $scope.master.surname,
                    title: $scope.master.title != null ? ($scope.master.title.length != 0 ? $scope.master.title : "") : "",
                    birth: $scope.master.birth != null ? commonTools.formatDateForRest($scope.master.birth) : "",
                    email: $scope.master.email != undefined ? ($scope.master.email.length != 0 ? $scope.master.email : "") : "",
                    phone: $scope.master.phone != undefined ? ($scope.master.phone.length != 0 ? $scope.master.phone : "") : "",
                    address: $scope.master.address != undefined ? ($scope.master.address.length != 0 ? $scope.master.address : "") : ""
                };
                $scope.messageBuilder = 'You have successfully updated these fields [';
                $scope.updatingItem = {};
                if (data.name != $scope.genuineEmployee.name) {
                    $scope.updatingItem.name = data.name;
                    $scope.messageBuilder += 'name, ';
                }
                if (data.surname != $scope.genuineEmployee.surname) {
                    $scope.updatingItem.surname = data.surname;
                    $scope.messageBuilder += 'surname, ';
                }
                if (!((data.title == "")
                    && ($scope.genuineEmployee.title == undefined || $scope.genuineEmployee.title == ""))) {
                    if ($scope.genuineEmployee.title != data.title) {
                        $scope.updatingItem.title = data.title;
                        $scope.messageBuilder += 'title, ';
                    }
                }
                if (!((data.birth == "")
                    && ($scope.genuineEmployee.birth == undefined || $scope.genuineEmployee.birth == ""))) {
                    //if birth have been filled or changed
                    var previousDate = ($scope.genuineEmployee.birth != undefined || $scope.genuineEmployee.birth != "") ? new Date($scope.genuineEmployee.birth) : undefined;
                    if(data.birth == "") {    //if birth have been removed (filled before, but not now)
                        $scope.updatingItem.birth = commonTools.formatDateForRest(new Date(1930,0,1));
                        $scope.messageBuilder += 'birth, ';
                    } else {
                        //if birth is now filled
                        if (previousDate == undefined) {
                            //(not filled before, but now it is)
                            $scope.updatingItem.birth = data.birth;
                            $scope.messageBuilder += 'birth, ';
                        } else {
                            //(filled before and also now)
                            if (previousDate.getTime() != $scope.master.birth.getTime()) {
                                $scope.updatingItem.birth = data.birth;
                                $scope.messageBuilder += 'birth, ';
                            }
                        }
                    }
                }
                if (!((data.email == "")
                    && ($scope.genuineEmployee.email == undefined || $scope.genuineEmployee.email == ""))) {
                    if ($scope.genuineEmployee.email != data.email) {
                        $scope.updatingItem.email = data.email;
                        $scope.messageBuilder += 'email, ';
                    }
                }
                if (!((data.phone == "")
                    && ($scope.genuineEmployee.phone == undefined || $scope.genuineEmployee.phone == ""))) {
                    if ($scope.genuineEmployee.phone != data.phone) {
                        $scope.updatingItem.phone = data.phone;
                        $scope.messageBuilder += 'phone, ';
                    }
                }
                if (!((data.address == "")
                    && ($scope.genuineEmployee.address == undefined || $scope.genuineEmployee.address == ""))) {
                    if ($scope.genuineEmployee.address != data.address) {
                        $scope.updatingItem.address = data.address;
                        $scope.messageBuilder += 'address, ';
                    }
                }
                if ($scope.messageBuilder.includes(', ')) {
                    $http({
                        url: '/posta/rest/employees/update/' + $scope.genuineEmployee.id,
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

        $scope.removeEmployee = function () {
            if($scope.genuineEmployee == null || $scope.genuineEmployee.id == undefined) {
                confirm('Cannot remove Employee immediately.\nFor removing employee we need to know unique identification, which is not available now.');
                $location.path("/employees");
            } else {
                if(confirm('You are above to completely remove Employee:\n'+ commonTools.formatNameSurname($scope.genuineEmployee) +'\n\nAre you sure?')) {
                    $http({
                        url: '/posta/rest/employees/' + $scope.genuineEmployee.id,
                        method: "DELETE"
                    }).then(function () {
                        createUpdateTools.setAlerts([{type: 'success', title:'Removed!', msg: 'You have successfully deleted '+ commonTools.formatNameSurname($scope.genuineEmployee) +' from system.'}]);
                        $location.path("/employees");
                    }, function () {
                        createUpdateTools.setAlerts([{type: 'danger', title:'Cannot Remove!', msg: commonTools.formatNameSurname($scope.genuineEmployee) +' is assigned to some jobs.'}]);
                        $location.path("/employees");
                    })
                }
            }
        };

    }]);
