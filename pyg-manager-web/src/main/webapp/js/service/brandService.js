app.service('brandService', function ($http) {
    this.findAll = function () {
        return $http.get('../brand/findAllBrand');
    };

    this.findPage = function (pageNum, pageSize) {
        return $http.get('../brand/findAllBrandAddPage?pageNum=' + pageNum + '&pageSize=' + pageSize)
    };

    this.save = function (x) {
        var methodName = 'addBrand';

        if (x.id != null) {
            methodName = 'updateBrand';
        }

        return $http.post('../brand/' + methodName, x)
    };

    this.findOne = function (x) {
        return $http.get('../brand/findBrandById?id=' + x)
    };

    this.delete = function (x) {
        return $http.get('../brand/deleteBrands?ids=' + x)
    };

    this.search = function (pageNum,pageSize,x) {
        return $http({
            url: '../brand/searchBrand?pageNum=' + pageNum + '&pageSize=' + pageSize,
            method: 'get',
            params: x
        });
    };

    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList')
    }
});