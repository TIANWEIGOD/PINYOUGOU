app.controller("payController", function (payService, $location, $scope) {
    $scope.createNative = function () {
        $scope.flag = false;
        payService.createNative().success(function (response) {
            // 金额
            $scope.money = (response.total_fee / 100).toFixed(2);
            // 订单id
            $scope.out_trade_no = response.out_trade_no;

            new QRious({
                element: document.getElementById("qrious"),
                size: 300,
                level: 'H',
                value: response.code_url
            });

            $scope.queryPayStatus($scope.out_trade_no);
        });
    };


    // 支付是否完成
    $scope.queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if (response.success) {
                location.href = "paysuccess.html#?money=" + $scope.money;
            } else {

                if (response.message == '二维码超时') {
                    $scope.flag = true;
                } else {
                    location.href = "payfail.html";
                }
            }
        })
    };

    $scope.showMoney = function () {
        $scope.total_fee = $location.search()['money'];
    }
});