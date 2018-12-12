app.controller("seckillController", function (seckillService, $location, $interval, $scope) {

    /*$scope.second = 10;

    time = $interval(function () {
        if ($scope.second > 0) {
            $scope.second = $scope.second - 1;
        } else {
            $interval.cancel(time);
        }
    }, 1000);*/


    $scope.findSeckillGoods = function () {
        seckillService.findSeckillGoods().success(function (response) {
            $scope.seckillGoodList = response;
        })
    };

    $scope.findOne = function () {

        var id = $location.search()['id'];

        if (id > 0) {
            seckillService.findOne(id).success(function (response) {
                $scope.seckillGood = response;
                var endTime = new Date($scope.seckillGood.endTime);
                var nowTime = new Date();

                $scope.seconds = Math.floor((endTime - nowTime) / 1000);

                var time = $interval(function () {
                    if ($scope.seconds > 0) {
                        $scope.seconds = $scope.seconds - 1;
                        $scope.timeString = $scope.convertTimeString($scope.seconds);
                    } else {
                        $interval.cancel(time);
                    }
                }, 1000);

                $scope.convertTimeString = function (allseconds) {
                    //计算天数
                    var days = Math.floor(allseconds / (60 * 60 * 24));

                    //小时
                    var hours = Math.floor((allseconds - (days * 60 * 60 * 24)) / (60 * 60));

                    //分钟
                    var minutes = Math.floor((allseconds - (days * 60 * 60 * 24) - (hours * 60 * 60)) / 60);

                    //秒
                    var seconds = allseconds - (days * 60 * 60 * 24) - (hours * 60 * 60) - (minutes * 60);

                    //拼接时间
                    var timString = "";
                    if (days > 0) {
                        timString = days + "天:";
                    }
                    return timString += hours + ":" + minutes + ":" + seconds;
                }
            })
        }
    };

    // 抢购商品添加订单
    $scope.addOrder = function (id) {
        seckillService.addOrder(id).success(function (response) {
            if (response.success) {
                location.href = "pay.html";
            } else {
                alert(response.message);
            }
        })
    }


});