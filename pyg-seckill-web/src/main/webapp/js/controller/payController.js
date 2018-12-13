app.controller('payController', function (payService, $scope) {
    $scope.createNative = function () {
        $scope.flag = false;
        payService.createNative().success(function (response) {
            $scope.money = (response.total_fee / 100).toFixed(2);
            $scope.out_trade_no = response.out_trade_no;
            new QRious({
                element: document.getElementById("qrious"),
                size: 300,
                level: "H",
                value: response.code_url
            });

            queryPayStatus($scope.out_trade_no);
        })
    };

    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if (response.success) {
                location.href = "paysuccess.html#?money=" + $scope.money;
            } else {
                $scope.flag = true;
                alert(response.message);
            }
        })
    }
});