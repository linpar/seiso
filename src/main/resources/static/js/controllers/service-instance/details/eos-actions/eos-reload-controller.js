var eosReloadController = function() {
	var controller = function($scope, $http, $routeParams) {
		var serviceInstanceKey = $routeParams.key;
		$scope.submit = function() {
			console.log("Reloading");
			$scope.reloadStatus = 'loading';
			var path = "/internal/service-instances/" + serviceInstanceKey + "/reload";
			var successHandler = function(data) {
				console.log("Success");
				$scope.reloadStatus = 'success';
			}
			var errorHandler = function() {
				console.log("Error");
				$scope.reloadStatus = 'error';
			}
			$http.post(path)
					.success(successHandler)
					.error(errorHandler);
		}
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
