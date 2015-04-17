var motdController = function() {
	var controller = function($scope, $http) {
		var successHandler = function(data) {
			console.log(data);
			if (data.message) {
				$scope.motd = data.message;
			}
		}
		var errorHandler = function() { $scope.motdError = true; }
		
		console.log('Getting motd');
		var request = {
			method: 'GET',
			url: '/internal/motd',
			headers: { 'Accept': 'application/hal+json' }
		}
		$http(request)
				.success(successHandler)
				.error(errorHandler);
	}
	return [ '$scope', '$http', controller ];
};
