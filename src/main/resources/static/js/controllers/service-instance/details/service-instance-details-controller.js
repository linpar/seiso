var serviceInstanceDetailsController = function() {
	var controller = function($scope, v2Api, $routeParams) {
		var successHandler = function(data) {
			var serviceInstance = data;
			var siEmbedded = serviceInstance._embedded;
			var service = siEmbedded.service;
			$scope.serviceInstance = serviceInstance;
			$scope.model.page.title = pageTitle(serviceInstance.key);
			$scope.dataCenter = siEmbedded.dataCenter;
			$scope.environment = siEmbedded.environment;
			$scope.ipAddressRoles = siEmbedded.ipAddressRoles;
			$scope.loadBalancer = siEmbedded.loadBalancer;
			$scope.ports = siEmbedded.ports;
			$scope.service = service;
			$scope.owner = service._embedded.owner;
			$scope.dashboards = siEmbedded.dashboards;
			$scope.checks = siEmbedded.seyrenChecks;
		}
		// TODO Make the error handling like other controllers'.
		var errorHandler = function() { alert("Error while getting service instance."); }
		v2Api.get('/v2/service-instances/' + $routeParams.key, successHandler, errorHandler);
	}
	
	return [ '$scope', 'v2Api', '$routeParams', controller ];
}
