app.controller('indexController',function (indexService, $scope) {
    $scope.findByCategotyId = function (categoryId) {
        indexService.findByCategotyId(categoryId).success(function (response) {
            $scope.brannerList = response;
        })
    }
});