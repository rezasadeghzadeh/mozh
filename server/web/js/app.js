/**
 * Created by reza on 11/28/16.
 */
var app = angular.module('app', ['ngTouch','ui.grid','ui.grid.resizeColumns']);
app.controller('MainCtrl', ['$scope', function ($scope) {

}]);
//serverUrl  =  "http://127.0.0.1:7777";
serverUrl  =  "http://ochm.yourlocalplace.com:7777";
app.controller('FetchItemCtrl', function($scope, $http) {

    $scope.gridOption ={
        enableColumnResizing: true,
        data : 'myData',
        columnDefs: [{
            field: 'id'
        },{
            field: 'Title'
        },{
            field: 'Description',
            width: 400
        },{
            field: 'CityTitle'
        },{
            field: 'CategoryTitle'
        },{
            field: 'Date',
        },{
            field: 'Address'
        },{
            name: 'Image',
            cellClass: 'imageCell',
            cellTemplate: 'imageLinkTemplate.html'
        },{
            field: 'Approve',
            cellTemplate: 'approveTemplate.html'
        }]
    };
    fetchData();

    function fetchData(){
        //url  =  serverUrl +"/item/list"
        url  =  serverUrl + "/item/list?Approved=false"
        $http.get(url).then(function( response){
            $scope.myData = response.data;
        });
    }

    $scope.approveItem = function(id){
        //url = serverUrl  + '/item/approve?Id=' + id,
        url = serverUrl  + '/item/approve?Id=' + id,

        $http.get(url).then(function( response){
            alert("Approved Successfully");
            fetchData();
        });
    };

});