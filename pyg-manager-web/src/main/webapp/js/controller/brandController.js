app.controller('brandController', function ($scope,brandService,$controller) {

    $controller('baseController',{$scope:$scope});

    $scope.findAllBrand = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            });
    };

    // 分页操作
    // 分页空间配置
    /*$scope.paginationConf = {
        currentPage: 1, // 当前页
        totalItems: 10, // 总记录数
        itemsPerPage: 10, // 当前页有几条记录
        perPageOptions: [10, 20, 30, 40, 50], // 选择当前页有几条记录
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };*/
    // 从新加载
    /*$scope.reloadList = function () {
        // 切换页码
        // $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
    };*/

    // 分页
    $scope.findPage = function (pageNum, pageSize) {
        brandService.findPage(pageNum, pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total
            });
    };

    // 添加/修改
    $scope.save = function () {
        brandService.save($scope.entity).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
            });
    };

    // 查询一个信息
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            });
    };

    // 删除模块
    /*$scope.selectIds = [];*/

    /*$scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);
        }
    };*/

    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message)
                }
            });
    };

    // 条件查询
    /*$scope.searchEntity = {};*/
    $scope.search = function (pageNum, pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    }
});