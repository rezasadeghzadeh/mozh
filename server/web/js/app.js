/**
 * Created by reza on 11/28/16.
 */
var app = angular.module('app', ['ngTouch','ui.grid','ui.grid.resizeColumns']);
app.controller('MainCtrl', ['$scope', function ($scope) {

}]);

app.controller('FetchItemCtrl', function($scope, $http) {
    $scope.gridOption ={
        enableColumnResizing: true,
        data : 'myData',
        columnDefs: [{
            field: 'Title'
        },{
            field: 'Description',
            width: 400
        },{
            field: 'CityTitle'
        },{
            field: 'CategoryTitle'
        },{
            field: 'Date'
        },{
            field: 'Address'
        },{
            field: 'Image'
        },{
            field: 'Approved'
        }]
    }
    $http.get("http://ochm.yourlocalplace.com:7777/item/list").then(function( response){
           $scope.myData = response.data;
       });
})