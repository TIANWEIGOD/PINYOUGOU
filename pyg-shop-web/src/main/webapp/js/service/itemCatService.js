//服务层
app.service('itemCatService', function ($http) {


    this.findByParentId = function (parentId) {
        return $http.get('../itemCat/findByParentId/' + parentId);//TODO 测试数据
    };

    //读取列表数据绑定到表单中
    this.findAll = function () {
        return $http.get('../itemCat/findAll');
    };
    //分页
    this.findPage = function (pageNum, pageSize) {
        return $http.get('../itemCat/findPage/' + pageNum + "/" + pageSize);
    };

    //查询一个
    this.findOne = function (id) {
        return $http.get('../itemCat/findOne/' + id);
    }
});
