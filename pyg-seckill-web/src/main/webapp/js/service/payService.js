app.service('payService', function ($http) {
    this.createNative = function () {
        return $http.get("./seckillPay/createNative")
    };

    this.queryPayStatus = function (out_trade_no) {
        return $http.get("./seckillPay/queryPayStatus?out_trade_no=" + out_trade_no)
    }
});