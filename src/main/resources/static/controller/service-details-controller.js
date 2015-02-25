angular.module('seisoControllers').controller('ServiceDetailsController', [ '$scope', '$http', '$routeParams',
	function($scope, $http, $routeParams) {
		console.log("Getting service");
		$scope.pageLoading = true;
		$http.get('v1/services/' + $routeParams.key).success(function(data) {
			$scope.service = data;
			$scope.pageLoading = false;
		});
		
		console.log("Getting service instances");
		$http.get('v1/services/' + $routeParams.key + '?view=instances').success(function(data) {
			console.log("Got service instances");
			$scope.serviceInstances = data.serviceInstances;
		});
		
		console.log("Getting service dependencies");
		$http.get('v1/services/' + $routeParams.key + '?view=dependencies').success(function(data) {
			$scope.serviceDependencies = data.serviceDependencies;
		});
	} ]);
