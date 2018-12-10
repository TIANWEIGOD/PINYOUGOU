app.controller('cartController', function (cartService, $location, $scope) {

    $scope.entity = {};

    // 支付类型，1、在线支付，2、货到付款
    $scope.order = {paymentType: '1'}

    // 查询购物车里的商品
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            $scope.totalNum = 0;
            $scope.totalMoney = 0.00;
            for (var i = 0; i < response.length; i++) {
                var orderItemList = response[i].orderItemList;
                for (var j = 0; j < orderItemList.length; j++) {
                    var orderItem = orderItemList[j];
                    $scope.totalNum += orderItem.num;
                    $scope.totalMoney += orderItem.totalFee;

                }
            }
        })
    };

    // 往购物车添加商品
    $scope.addGoodsToCartList = function (id, num) {
        cartService.addGoodsToCartList(id, num).success(function (response) {
            if (response.success) {
                $scope.findCartList();
            } else {
                alert(message);
            }
        });
    };

    // 根据用户名查询该用户的地址
    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;

            for (var i = 0; i < response.length; i++) {
                if (response[i].isDefault == '1') {
                    $scope.address = response[i];
                    break;
                }

            }
        });
    };

    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    $scope.ifSelectedAddress = function (address) {
        if ($scope.address == address) {
            return true;
        } else {
            return false;
        }
    };

    // 添加订单
    $scope.submitOrder = function () {

        $scope.order.receiverAreaName = $scope.address.address; // 地址
        $scope.order.receiverMobile = $scope.address.mobile; // 手机
        $scope.order.receiver = $scope.address.contact; // 联系人

        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success) {
                if ($scope.order.paymentType == '1') {
                    location.href = "http://pay.pinyougou.com/pay.html";
                } else {
                    location.href = "http://pay.pinyougou.com/paysuccess.html";
                }
            } else {
                alert(response.message);
            }
        })
    };
});
