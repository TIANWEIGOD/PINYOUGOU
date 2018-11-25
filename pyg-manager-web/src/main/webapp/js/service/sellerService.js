//服务层
app.service('sellerService',function($http){
	    	
	
	this.updateStatus=function(status,sellerId){
		return $http.get('../seller/updateStatus?status='+status+"&id="+sellerId);
		
	};
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seller/findAll');		
	};
	//分页 
	this.findPage=function(pageNum,pageSize){
		return $http.get('../seller/findPage/'+pageNum+"/"+pageSize);
	};
	//查询实体
	this.findOne=function(id){
		return $http.get('../seller/findOne?id='+id);
	};
	//增加 
	this.add=function(entity){
		return  $http.post('../seller/add',entity);
	};
	//修改 
	this.update=function(entity){
		return  $http.post('../seller/update',entity );
	};
	//删除
	this.dele=function(ids){
		return $http.get('../seller/delete/'+ids);
	};
	//搜索
	this.search=function(pageNum,pageSize,searchEntity){
		return $http.post('../seller/search?page='+pageNum+"&rows="+pageSize, searchEntity);
	}    	
});
