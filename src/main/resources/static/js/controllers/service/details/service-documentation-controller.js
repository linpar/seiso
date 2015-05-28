var serviceDocumentationController = function() {
	var controller = function($scope, v2Api, $routeParams) {
		$scope.serviceDocumentationStatus = 'loading';
		var path = "/v2/services/" + $routeParams.key + "/doc-links";
		var successHandler = function(data) {
			$scope.docLinks = data;
			$scope.serviceDocumentationStatus = 'loaded';
		}
		var errorHandler = function() {
			$scope.serviceDocumentationStatus = 'error';
		}
		v2Api.get(path, successHandler, errorHandler);
	}
	return [ '$scope', 'v2Api', '$routeParams', controller ];
}
