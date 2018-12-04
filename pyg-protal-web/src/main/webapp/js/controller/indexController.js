app.controller('indexController',function (indexService, $scope) {
    $scope.findByCategotyId = function (categoryId) {
        indexService.findByCategotyId(categoryId).success(function (response) {
            $scope.brannerList = response;
        })
    };

    $scope.search = function () {
        location.href="http://search.pinyougou.com/search.html#?keyword="+$scope.keyword;
    }
});