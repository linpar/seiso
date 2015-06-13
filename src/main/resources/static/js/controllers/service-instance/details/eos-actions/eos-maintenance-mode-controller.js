var eosMaintenanceModeController = function() {
	var controller = function($scope, $http, $routeParams) {
		var serviceInstanceKey = $routeParams.key;
		$scope.form = {};
		$scope.submit = function() {
			console.log("Setting maintenance mode");
			$scope.maintenanceModeStatus = 'loading';
			var path = "/internal/service-instances/" + serviceInstanceKey + "/maintenance-mode";
			var requestBody = {
				"nodeList" : $scope.form.nodeList,
				"minutes" : $scope.form.minutes,
				"enable" : $scope.form.enable,
				"overrideOthers" : $scope.form.overrideOthers
			}
			console.log(requestBody);
			var successHandler = function(data) {
				console.log("Success");
				$scope.maintenanceModeStatus = 'success';
			}
			var errorHandler = function() {
				console.log("Error");
				$scope.maintenanceModeStatus = 'error';
			}
			$http.post(path, requestBody)
					.success(successHandler)
					.error(errorHandler);
		}
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
