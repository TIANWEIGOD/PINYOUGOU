app.service('userService', function ($http) {

    this.sendSms = function (phone) {
        return $http.get("./user/sendSms?phone=" + phone);
    };

    this.add = function (user, code) {
        return $http.post("./user/add?code=" + code, user);
    };

    this.showName = function () {
        return $http.get("./login/showName");
    }
});