app.controller("payController", function (payService, $scope) {
    $scope.createNative = function () {
        payService.createNative().success(function (response) {

            // 金额
            $scope.money = response.total_fee;
            // 订单id
            $scope.out_trade_no = response.out_trade_no;

            var qr = new QRious({
                element: document.getElementById("qrious"),
                size: 250,
                level: 'H',
                value: response.code_url
            });

            queryPayStatus($scope.out_trade_no);
        });
    };


    // 支付是否完成
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if (response.success){
                location.href = "paysuccess.html";
            } else {
                location.href = "payfail.html";
            }
        })
    }
});