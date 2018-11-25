//抽取服务
app.service("loginService", function($http) {

    this.loadLoginName = function () {
        return $http.get("../login/loadLoginName");
    }
});
