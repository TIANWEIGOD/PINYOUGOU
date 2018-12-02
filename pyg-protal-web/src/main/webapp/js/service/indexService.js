app.service('indexService',function ($http) {
    this.findByCategotyId = function (categoryId) {
        return $http.get('./content/findByCategotyId/'+categoryId);
    }
});