app.controller("baseController",function($scope){
	
	$scope.paginationConf = {
			 currentPage: 1,  //当前页码   是angular控制
			 totalItems: 10,  //总条数     应该从后台查询 赋值
			 itemsPerPage: 10, //每页显示的条数  是angular控制
			 perPageOptions: [10, 20, 30, 40, 50],  //每页显示的条数的选择  是angular控制
			 onChange: function(){
			     $scope.reloadList();//重新加载
			 }
	};
	
	
	$scope.searchEntity={};
	
    $scope.reloadList=function(){
//    	 分页查询  当前页码  每页显示的条数
//   	 $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
   	 	 $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };
    
//	 准备一个数组 来接收即将删除品牌的id
	 $scope.selectIds=[];
	 
//	 点击复选框时更改数组中的值
	$scope.updateSelection=function(event,id){
//		event.target 代表的时正在操作的对象  一个复选框
		if(event.target.checked){
			$scope.selectIds.push(id);	// 	    向数组中放值
		}else{
//	 	    从向数组中移除值 
//             	获取值在数组中的位置
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);	// 	    向数组中放值两个参数 p1:即将移除的值在数组中的索引值  p2：删除的个数
		}
//               
	};
	
	
//	点击"全选"复选框的
	$scope.checkedAll=function(event){
		if(event.target.checked){
	//		把当前页显示的list中的每个对象的id放到 $scope.selectIds里
			for (var i = 0; i < $scope.list.length; i++) {
				if($scope.selectIds.indexOf($scope.list[i].id)==-1){
					$scope.selectIds.push($scope.list[i].id);
				}
				
				
			}
		}else{
//			把当前页显示的list中的每个对象的id从 $scope.selectIds中移除
			for (var i = 0; i < $scope.list.length; i++) {
				var index = $scope.selectIds.indexOf($scope.list[i].id);
				$scope.selectIds.splice(index,1);
			}
//			$scope.selectIds=[];
		}
	};
	
//	判断id是否在数组中
	$scope.isChecked=function(id){
		if($scope.selectIds.indexOf(id)==-1){
			return false;
		}
		return true;
	};
	
//	判断当前页的list中的每个对象的id是否都在数组中  如果都在返回true 但凡有一个不在就应该返回false
	$scope.isCheckedAll=function(){
		for (var i = 0; i < $scope.list.length; i++) {
			var index = $scope.selectIds.indexOf($scope.list[i].id);
			if(index==-1){
				 return false;
			}
		}
		return true;
	};

    $scope.jsonToString = function (jsonString, key) {
		var json = JSON.parse(jsonString);
		var value = "";

		for(var i=0;i<json.length;i++){

			if (i>0){
				value+=",";
			}

			value+=json[i][key];
		}

		return value;
    }
});