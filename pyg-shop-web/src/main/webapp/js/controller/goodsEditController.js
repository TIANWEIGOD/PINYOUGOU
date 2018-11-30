app.controller("goodsEditController", function ($scope, uploadService, goodsService, itemCatService, typeTemplateService) {

    $scope.entity = {tbGoods: {isEnableSpec: '1'}, tbGoodsDesc: {itemImages: [], specificationItems: []}};

    $scope.findCategory1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.category1List = response;
            })
    };

    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.category2List = response;
            });
    });

    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.category3List = response;
            });
    });

    $scope.$watch('entity.tbGoods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.tbGoods.typeTemplateId = response.typeId;
            });
    });

    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldVale) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.brandList = JSON.parse(response.brandIds);
                $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
                // $scope.entity.isEnableSpec = JSON.parse(response.specIds);
            });
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            });
    });


    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image.url = response.message;
                } else {
                    alert(response.message);
                }
            });
    };

    // 网集合里面添加选择的东西
    $scope.updateSpecificationItems = function (event, key, value) {

        // 先判断集合中是否包含key
        var specItem = findObjectFromArray($scope.entity.tbGoodsDesc.specificationItems, key);
        if (event.target.checked) {
            if (specItem == null) {
                $scope.entity.tbGoodsDesc.specificationItems.push({attributeName: key, attributeValue: [value]});
            } else {
                specItem.attributeValue.push(value);
            }
        } else {
            var index = specItem.attributeValue.indexOf(value);
            specItem.attributeValue.splice(index, 1);

            if (specItem.attributeValue.length == 0) {
                var _index = $scope.entity.tbGoodsDesc.specificationItems.indexOf(specItem);
                $scope.entity.tbGoodsDesc.specificationItems.splice(_index, 1);
                if ($scope.entity.tbGoodsDesc.specificationItems.length == 0) {
                    $scope.entity.itemList = [];
                    return;
                }
            }
        }

        createItemList();
        // [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]


        /*if ($scope.entity.tbGoodsDesc.specificationItems.length == 0) {
            $scope.entity.tbGoodsDesc.specificationItems.push({attributeName: key, attributeValue: [value]});
        } else {
            for (var i = 0; i < $scope.entity.tbGoodsDesc.specificationItems.length; i++) {
                var keys = $scope.entity.tbGoodsDesc.specificationItems[i].attributeName;
                var attributeValueList = $scope.entity.tbGoodsDesc.specificationItems[i].attributeValue;
                if (key == keys) {
                    attributeValueList.push(value);
                }
            }

        }*/
    };

    function findObjectFromArray(specificationItems, key) {
        for (var i = 0; i < specificationItems.length; i++) {
            if (specificationItems[i].attributeName == key) {
                return specificationItems[i];
            }
        }
        return null;
    }


    // 动态生成sku列表   entity.itemList
    function createItemList() {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: "1", isDefault: '0'}];
        var specItems = $scope.entity.tbGoodsDesc.specificationItems;
        //     [{attributeName:'网络',attributeValue:['移动4G','联通4G']},
        //         {attributeName:'机身内存',attributeValue:['32G','64G']}]
        for (var i = 0; i < specItems.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, specItems[i].attributeName, specItems[i].attributeValue);
        }
    }

    function addColumn(itemList, attributeName, attributeValue) {
        var newItemList = [];
        for (var i = 0; i < itemList.length; i++) {
            for (var j = 0; j < attributeValue.length; j++) {
                var newRow = JSON.parse(JSON.stringify(itemList[i]));
                newRow.spec[attributeName] = attributeValue[j];    //---{网络：移动4G}
                newItemList.push(newRow);
            }
        }
        return newItemList;
    }


    // 上传图片保存到entity.tbGoodsDesc.itemImages数组中
    $scope.addItemImage = function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image);
        $("#file").val("");
    };

    $scope.deleItemImages = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
    };

    $scope.save = function () {
        /*$scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(function (response) {
            if (response.success) {
                location.href = "goods.html";  //跳转到列表页面
            } else {
                alert(response.message);
            }
        });*/
        // 获取富文本编辑器上的值并给属性附上该值
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        /*alert($scope.entity);*/
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    location.href = "goods.html";
                } else {
                    alert(response.message);
                }
            })

    }
});


/*
$scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}}; //初始化itemImages 是个数组
//	图片上传
$scope.uploadFile=function(){
    uploadService.upload().success(function(response){
        if(response.success){
            alert("上传成功后返回图片地址："+response.message);
        }else{
            alert(response.message);
        }

    })
};

//	向entity.tbGoods.itemImages中追加对象
$scope.addItemImage=function(){

};

//	从entity.tbGoods.itemImages中移除对象
$scope.deleItemImages=function(index){

};


//	查询一级分类数据
$scope.findCategory1List=function(){
    itemCatService.findByParentId(0).success(function(response){
        $scope.category1List = response;
    })
};
//	检测数据的变化entity.tbGoods.category1Id 查询二级分类数据  相当于onChange事件
$scope.$watch('entity.tbGoods.category1Id',function(newValue,oldValue){
    itemCatService.findByParentId(newValue).success(function(response){
        $scope.category2List = response;
    })
});

//	检测数据的变化entity.tbGoods.category2Id 查询三级分类数据  相当于onChange事件
$scope.$watch('entity.tbGoods.category2Id',function(newValue,oldValue){
    itemCatService.findByParentId(newValue).success(function(response){
        $scope.category3List = response;
    })
});
//	检测数据的变化entity.tbGoods.category3Id 查询模板ID  相当于onChange事件
$scope.$watch('entity.tbGoods.category3Id',function(newValue,oldValue){
    if(newValue){
        alert("根据第三级分类对象查询模板id");
        $scope.entity.tbGoods.typeTemplateId=35;
    }


});

//	检测数据的变化$scope.entity.tbGoods.typeTemplateId 查询品牌数据  相当于onChange事件
$scope.$watch('entity.tbGoods.typeTemplateId',function(newValue,oldValue){
    typeTemplateService.findOne(newValue).success(function(response){
//			从模板中获取品牌
        $scope.brandList = response.brandIds;
//			从模板中获取扩展属性
        $scope.entity.tbGoodsDesc.customAttributeItems = response.customAttributeItems;
    });

    typeTemplateService.findSpecList(newValue).success(function(response){
//			response = [{"id":27,"text":"网络",options:[{},{},{}]},{"id":32,"text":"机身内存"}]
        $scope.specList = response;
    })
});


//	勾选规格小项更改即将保存的数据
$scope.updateSpecificationItems=function(event,key,value){
//

    alert("点击规格项修改即将保存规格的数据格式");
//
};

//	根据$scope.entity.tbGoodsDesc.specificationItems动态生成sku列表
function createItemList(){
//	       第一步：初始化itemList  spec格式:{"机身内存":"16G","网络":"联通4G"}
    $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'1',isDefault:'0'}];
    var specificationItems = $scope.entity.tbGoodsDesc.specificationItems;
//		第二步：循环items 为$scope.entity.itemList追加列
    for (var i = 0; i < specificationItems.length; i++) {
        $scope.entity.itemList = addColumn($scope.entity.itemList,specificationItems[i].attributeName,specificationItems[i].attributeValue);
    }

}
function addColumn(itemList,attributeName,attributeValue){
    var newItemList=[];
    for (var i = 0; i < itemList.length; i++) {
        for (var j = 0; j < attributeValue.length; j++) {
            var row = JSON.parse( JSON.stringify( itemList[i]));//深克隆
            row.spec[attributeName]=attributeValue[j];
            newItemList.push(row);
        }
    }
    return newItemList;
}


$scope.save=function(){
    $scope.entity.tbGoodsDesc.introduction = editor.html();
    goodsService.add($scope.entity).success(function(response){
        if(response.success){
            location.href="goods.html";  //跳转到列表页面
        }else{
            alert(response.message);
        }
    });

}*/
