var serviceInstanceDependenciesController = function() {
	var controller = function($scope, v2Api, $routeParams) {
		$scope.dependenciesStatus = 'loading';
		var siKey = $routeParams.key;
		var path = "/v2/service-instance-dependencies/search/find-by-dependent?key=" + siKey;
		var successHandler = function(data) {
			$scope.dependencies = data._embedded.items;
			$scope.dependenciesStatus = 'loaded';
		}
		var errorHandler = function() {
			$scope.dependenciesStatus = 'error';
		}
		v2Api.get(path, successHandler, errorHandler);
	}
	return [ '$scope', 'v2Api', '$routeParams', controller ];
}
