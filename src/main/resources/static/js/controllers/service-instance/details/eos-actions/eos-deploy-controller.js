var eosDeployController = function() {
	var controller = function($scope, $http, $routeParams) {
		var serviceInstanceKey = $routeParams.key;
		$scope.form = {};
		$scope.submit = function() {
			console.log("Deploying");
			$scope.deployStatus = 'loading';
			var path = "/internal/service-instances/" + serviceInstanceKey + "/deploy";
			var requestBody = {
				"version" : $scope.form.version,
				"arguments" : $scope.form.arguments,
				"nodeList" : $scope.form.nodeList,
				"deploySameVersion" : $scope.form.deploySameVersion,
				"overrideStateRestriction" : $scope.form.overrideStateRestriction,
				"skipRotateIn" : $scope.form.skipRotateIn,
				"skipRotateOut" : $scope.form.skipRotateOut,
				"skipDvt" : $scope.form.skipDvt,
				"skipSetActive" : $scope.form.skipSetActive
			}
			console.log(requestBody);
			var successHandler = function(data) {
				console.log("Success");
				$scope.deployStatus = 'success';
			}
			var errorHandler = function() {
				console.log("Error");
				$scope.deployStatus = 'error';
			}
			$http.post(path, requestBody)
					.success(successHandler)
					.error(errorHandler);
		}
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
