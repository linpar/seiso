var eosActionController = function() {
	var controller = function($scope, $http, $routeParams) {
		var serviceInstanceKey = $routeParams.key;
		$scope.actions = {
			convict: function() {
				console.log("Convicting");
			},
			interrogate: function() {
				console.log("Interrogating");
				var path = "/internal/service-instances/" + serviceInstanceKey + "/interrogate";
				var successHandler = function(data) {
					console.log("Success");
				}
				var errorHandler = function() {
					console.log("Error");
					// TODO Do a better notification.
					alert("Interrogation failed.");
				}
				$http.post(path)
						.success(successHandler)
						.error(errorHandler);
			},
			soak: function() {
				console.log("Soaking");
			}
		}
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
