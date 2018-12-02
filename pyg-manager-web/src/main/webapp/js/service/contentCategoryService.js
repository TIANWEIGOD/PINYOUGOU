//服务层
app.service('contentCategoryService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../contentCategory/findAll');
	};
	//分页 
	this.findPage=function(pageNum,pageSize){
		return $http.get('../contentCategory/findPage/'+pageNum+"/"+pageSize);
	};
	//查询实体
	this.findOne=function(id){
		return $http.get('../contentCategory/findOne?id='+id);
	};
	//增加 
	this.add=function(entity){
		return  $http.post('../contentCategory/add',entity );
	};
	//修改 
	this.update=function(entity){
		return  $http.post('../contentCategory/update',entity );
	};
	//删除
	this.dele=function(ids){
		return $http.get('../contentCategory/delete?ids='+ids);
	};
	//搜索
	this.search=function(pageNum,pageSize,searchEntity){
		return $http.post('../contentCategory/search?page='+pageNum+"&rows="+pageSize, searchEntity);
	}    	
});
