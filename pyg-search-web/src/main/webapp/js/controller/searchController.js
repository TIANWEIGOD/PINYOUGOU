app.controller('searchController', function (searchService,$location, $scope) {

    $scope.paramMap = {keyword: '小米', category: '', brand: '', price: '', order: 'asc', spec: {}, pageNo: 1};

    $scope.initSearch = function () {
        if ($location.search()['keyword'] != undefined) {
            $scope.paramMap.keyword = $location.search()['keyword'];
            $scope.keyword = $location.search()['keyword'];
        }
        $scope.search();

    };

    // 根据关键字查询
    $scope.searchByKeyword = function () {
        $scope.paramMap = {keyword: '小米', category: '', brand: '', price: '', order: 'asc', spec: {}, pageNo: 1};
        $scope.paramMap.keyword = $scope.keyword;
        $scope.search()
    };

    // 加条件查询
    $scope.addParamToParamMap = function (key, value) {
        $scope.paramMap[key] = value;
        $scope.search();
    };

    $scope.addSpecParamToParamMap = function (key, value) {
        $scope.paramMap.spec[key] = value;
        $scope.search();
    };

    $scope.removeParamToParamMap = function (key) {
        $scope.paramMap[key] = '';
        $scope.search();
    };

    $scope.removeSpecParamToParamMap = function (key) {
        delete $scope.paramMap.spec[key];
        $scope.search();
    };

    $scope.search = function () {
        searchService.searchByParam($scope.paramMap).success(function (response) {
            $scope.resultMap = response;

            $scope.pageLabel = [];
            /*for (var i = 1; i <= response.totalPages.length; i++) {
                $scope.pageLabel.push(i)

            }*/

            buildPageLabel();
        })
    };

    function buildPageLabel() {
        $scope.pageLabel = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点
        if ($scope.resultMap.totalPages > 5) { //如果总页数大于 5 页,显示部分页码
            if ($scope.paramMap.pageNo <= 3) {//如果当前页小于等于 3
                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没点
            } else if ($scope.paramMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4;  //后 5 页
                $scope.lastDot = false;//后边没点
            } else { //显示当前页为中心的 5 页
                firstPage = $scope.paramMap.pageNo - 2;
                lastPage = $scope.paramMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }
});