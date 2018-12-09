app.service('cartService', function ($http) {

    this.findCartList = function () {
        return $http.get("./cart/findCartList")
    };

    this.addGoodsToCartList = function (id, num) {
        return $http.get("./cart/addGoodsToCartList?itemId=" + id + "&num=" + num)
    };

    this.findAddressList = function () {
        return $http.get("./address/findListByUserId")
    };

    this.submitOrder = function (order) {
         return $http.post("./order/add",order)
    }
});