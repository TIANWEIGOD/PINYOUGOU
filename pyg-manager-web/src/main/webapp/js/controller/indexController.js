app.controller('indexController', function ($scope, $controller, indexService) {

    $controller('baseController',{$scope:$scope});

    $scope.name = function () {
        indexService.loginName().success(
            function (response) {
                $scope.username = response.loginName;
            })
    }

});