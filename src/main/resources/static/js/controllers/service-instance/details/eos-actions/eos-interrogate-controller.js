var eosInterrogateController = function() {
	var controller = function($scope, $http, $routeParams) {
		var serviceInstanceKey = $routeParams.key;
		$scope.form = {};
		$scope.submit = function() {
			console.log("Interrogating");
			$scope.interrogateStatus = 'loading';
			var path = "/internal/service-instances/" + serviceInstanceKey + "/interrogate";
			var requestBody = {
				"nodeList" : $scope.form.nodeList,
				"runDvt" : $scope.form.runDvt
			}
			console.log(requestBody);
			var successHandler = function(data) {
				console.log("Success");
				$scope.interrogateStatus = 'success';
			}
			var errorHandler = function() {
				console.log("Error");
				$scope.interrogateStatus = 'error';
			}
			$http.post(path, requestBody)
					.success(successHandler)
					.error(errorHandler);
		}
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
