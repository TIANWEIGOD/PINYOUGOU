app.service("uploadService",function($http){
//	angularJS的文件上传
	this.uploadFile=function(){
//		html5的对象
		var formData = new FormData();
		formData.append("file",file.files[0]); //file.files[0] js的取值方式  取到页面的第一个file
		return $http({
			method:'post',
			url:'../upload/uploadFile',
			data:formData,
			headers: {'Content-Type':undefined},  //Content-Type’: undefined，这样浏览器会帮我们把 Content-Type 设置为 multipart/form-data enctype类型一定是 multipart/form-data
			transformRequest: angular.identity  //序列化上传的文件
		});
	}
});