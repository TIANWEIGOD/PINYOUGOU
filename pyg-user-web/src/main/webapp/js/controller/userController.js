app.controller('userController', function (userService, $location, $scope) {
    $scope.user = {};

    $scope.sendSms = function (phone) {
        userService.sendSms(phone).success(function (response) {

        })
    };

    $scope.add = function () {
        userService.add($scope.user, $scope.code).success(function (response) {
            if (response.success) {
                // location.href = "http://www.baidu.com"
            } else {
                $scope.message = response.message;
            }
        })
    }
});