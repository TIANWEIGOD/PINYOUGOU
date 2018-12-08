app.controller('userController', function (userService, $location, $scope) {
    $scope.user = {sourceType:'1'};

    $scope.sendSms = function (phone) {
        userService.sendSms(phone).success(function (response) {
            if(response.success){
                alert("倒计时效果");
            } else {
                alert(response.message);
            }
        });
    };

    $scope.add = function () {
        userService.add($scope.user, $scope.code).success(function (response) {
            if (response.success) {
                location.href = "home-index.html"
                // alert("单点登录");
            } else {
                $scope.message = response.message;
            }
        });
    };

    $scope.showName = function () {
        userService.showName().success(function (response) {
            $scope.username = response.username;
        })
    }
});