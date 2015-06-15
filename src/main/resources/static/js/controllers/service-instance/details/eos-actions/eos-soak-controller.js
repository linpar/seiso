var eosSoakController = function() {
	var controller = function($scope, $http, $routeParams) {
		var serviceInstanceKey = $routeParams.key;
		$scope.form = {};
		$scope.submit = function() {
			console.log("Interrogating");
			$scope.soakStatus = 'loading';
			var path = "/internal/service-instances/" + serviceInstanceKey + "/soak";
			var requestBody = {
				"nodeList" : $scope.form.nodeList,
				"activate" : $scope.form.activate
			}
			console.log(requestBody);
			var successHandler = function(data) {
				console.log("Success");
				$scope.soakStatus = 'success';
			}
			var errorHandler = function() {
				console.log("Error");
				$scope.soakStatus = 'error';
			}
			$http.post(path, requestBody)
					.success(successHandler)
					.error(errorHandler);
		}
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
