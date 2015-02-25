angular.module('seisoControllers').controller('ServiceInstanceDetailsController', [ '$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
	$scope.pageLoading = true;
	var request = {
		method: 'GET',
		url: 'v2/service-instances/' + $routeParams.key,
		headers: { 'Accept': 'application/hal+json' }
	}
	$http(request).success(function(data) {
		$scope.serviceInstance = data;
		$scope.dataCenter = $scope.serviceInstance._embedded.dataCenter;
		$scope.environment = $scope.serviceInstance._embedded.environment;
		$scope.ipAddressRoles = $scope.serviceInstance._embedded.ipAddressRoles;
		$scope.loadBalancer = $scope.serviceInstance._embedded.loadBalancer;
		$scope.nodes = $scope.serviceInstance._embedded.nodes;
		$scope.ports = $scope.serviceInstance._embedded.ports;
		$scope.service = $scope.serviceInstance._embedded.service;
		$scope.owner = $scope.service._embedded.owner;
		
		// FIXME Generates NPE if owner is null. [WLW]
//		$scope.owner.name = $scope.owner.firstName + ' ' + $scope.owner.lastName;
		
		// Initialize node counts
		$scope.numHealthy = 0;
		$scope.numEnabled = 0;
		$scope.numHealthyGivenEnabled = 0;
		
		// Build the node table, which is really a list of IP addresses grouped by node. [WLW]
		var nodeRows = [];
		for (i = 0; i < $scope.nodes.length; i++) {
			var node = $scope.nodes[i];
			
			if (node.healthStatus == null) {
				node.healthStatus = {
					"key" : "unknown",
					"name" : "Unknown",
					"statusType" : { "key" : "warning" }
				}
			}
			
			// FIXME Shouldn't hardcode what's currently an Eos-specific health status.
			if (node.healthStatus.key.toLowerCase() == 'healthy') { $scope.numHealthy++; }
			
			var ipAddresses = node._embedded.ipAddresses;
			var nodeEnabled = true;
			
			if (ipAddresses.length == 0) {
				// Handle special case where there aren't any IP addresses.
				var nodeRow = {
					"name" : node.name,
					"displayName" : node.name,
					"version" : node.version,
					"healthStatus" : node.healthStatus,
					"showActions" : true
				}
				nodeRows.push(nodeRow);
				nodeEnabled = false;
			} else {
				// Handle case where there are IP addresses.
				for (j = 0; j < ipAddresses.length; j++) {
					var ipAddress = ipAddresses[j];
					var nodeRow = {
						"name" : node.name,
						"ipAddress" : ipAddress.ipAddress,
						"ipAddressRole" : ipAddress._embedded.ipAddressRole.name,
						"endpoints" : ipAddress._embedded.endpoints,
						"aggregateRotationStatus" : ipAddress._embedded.aggregateRotationStatus
					};
					if (j == 0) {
						// Distinguish name from display name. We want to filter by name, but display by
						// displayName.
						nodeRow.displayName = node.name;
						nodeRow.version = node.version,
						nodeRow.healthStatus = node.healthStatus;
						nodeRow.showActions = true;
					}
					nodeRows.push(nodeRow);
					
					if (ipAddress._embedded.aggregateRotationStatus.key != "enabled") {
						nodeEnabled = false;
					}
				}
			}
			
			if (nodeEnabled) {
				$scope.numEnabled++;
				if (node._embedded.healthStatus.key == 'Healthy') { $scope.numHealthyGivenEnabled++; }
			}
		}
		
		$scope.numNodes = $scope.nodes.length;
		$scope.percentHealthy = 100 * ($scope.numHealthy / $scope.numNodes);
		$scope.percentEnabled = 100 * ($scope.numEnabled / $scope.numNodes);
		$scope.percentHealthyGivenEnabled = 100 * ($scope.numHealthyGivenEnabled / $scope.numEnabled);
		$scope.nodeRows = nodeRows;
		
		$scope.dashboards = $scope.serviceInstance._embedded.dashboards;
		$scope.checks = $scope.serviceInstance._embedded.seyrenChecks;
		
		$scope.interrogate = function() {
			console.log("Publishing interrogate request");
			$http.post('v1/actions', { "code" : "interrogate", "nodeKeys" : [] }).success(function(data) {
				console.log("Success");
			});
		}
		$scope.convict = function() {
			alert("Convicting selected nodes");
		}
		$scope.deploy = function() {
			alert("Deploying to selected nodes");
		}
		$scope.setMaintenanceMode = function() {
			alert("Setting maintenance mode for selected nodes");
		}
		
		$scope.pageLoading = false;
	});	
} ]);
