app.service('seckillService', function ($http) {
    this.findSeckillGoods = function () {
        return $http.get("./seckillGood/findSeckillGoods")
    };

    this.findOne = function (id) {
        return $http.get("./seckillGood/findOne?id=" + id);
    };

    this.addOrder = function (id) {
        return $http.get("./seckillGood/addOrder?id=" + id);
    }
});