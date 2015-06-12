var serviceInstanceDetailsController = function() {
	var controller = function($scope, v2Api, $http, $routeParams) {
		$scope.serviceInstanceStatus = 'loading';
		var serviceInstanceKey = $routeParams.key;
		var path = "/v2/service-instances/" + serviceInstanceKey;
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
			
			$scope.tabs = [
				{ heading: 'Dashboard', content: 'dashboard/index' },
				{ heading: 'All Nodes', content: 'nodes/node-pane' },
				{ heading: 'Details', content: 'details/index' },
				{ heading: 'Dependencies', content: 'dependencies/dependencies-tables' }
//				{ heading: 'Actions', content: 'eos-actions/eos-actions' }
			];
			
			$scope.setTabContent = function(name) {
				$scope.tabContentUrl = "view/items/service-instance/details/" + name + ".html";
			}
			
			$scope.actions = {
				interrogate: function() {
					console.log("Interrogating service instance in Eos");
					var interrogatePath = "/internal/service-instances/" + serviceInstanceKey + "/interrogate";
					var interrogateSuccessHandler = function(data) {
						console.log("Success");
					}
					var interrogateErrorHandler = function() {
						console.log("Error");
						// TODO Do a better notification.
						alert("Interrogation failed.");
					}
					$http.post(interrogatePath)
							.success(interrogateSuccessHandler)
							.error(interrogateErrorHandler);
				},
				reload: function() {
					console.log("Reloading service instance in Eos");
					var reloadPath = "/internal/service-instances/" + serviceInstanceKey + "/reload";
					var reloadSuccessHandler = function(data) {
						console.log("Success");
					}
					var reloadErrorHandler = function() {
						console.log("Error");
						alert("Reload failed.");
					}
					$http.post(reloadPath)
							.success(reloadSuccessHandler)
							.error(reloadErrorHandler);
				}
			}
			
			$scope.serviceInstanceStatus = 'loaded';
		}
		var errorHandler = function() {
			$scope.serviceInstanceStatus = 'error';
		}
		v2Api.get(path, successHandler, errorHandler);
	}
	return [ '$scope', 'v2Api', '$http', '$routeParams', controller ];
}
