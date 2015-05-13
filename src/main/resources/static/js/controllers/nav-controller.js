var navController = function() {
	var controller = function($scope, $http) {
		var successHandler = function(data) {
			console.log("Got nav");
			$scope.nav = data;
		}
		var errorHandler = function() { $scope.navError = true; }
		
		console.log("Getting nav");
		var request = {
			method: 'GET',
			url: '/internal/nav',
			headers: { 'Accept': 'application/hal+json' }
		}
		$http(request)
				.success(successHandler)
				.error(errorHandler);
	}
	return [ '$scope', '$http', controller ];
};
