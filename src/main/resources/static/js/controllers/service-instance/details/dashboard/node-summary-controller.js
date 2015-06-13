var nodeSummaryController = function() {
	var controller = function($scope, $http, $routeParams) {
		$scope.nodeStatsStatus = 'loading';
		
		var request = {
			method: 'GET',
			url: '/v2/service-instances/' + $routeParams.key + '/node-summary',
			headers: { 'Accept': 'application/hal+json' }
		}
		
		var successHandler = function(data) {
			var nodeStats = data;
			enrichNodeStats(nodeStats);
			$scope.nodeStats = nodeStats;
			
			var numHealthy = nodeStats.numHealthy;
			var numUnhealthy = nodeStats.numNodes - numHealthy;
			var numEnabled = nodeStats.numEnabled;
			var numNotEnabled = nodeStats.numNodes - numEnabled;
			var numHealthyGivenEnabled = nodeStats.numHealthyGivenEnabled;
			var numUnhealthyGivenEnabled = numEnabled - numHealthyGivenEnabled;
			
			$scope.healthDataset = [
				{ type: 'Healthy', count: numHealthy }, 
				{ type: 'Unhealthy', count: numUnhealthy }
			];
			
			$scope.enabledDataset = [
				{ type: 'Enabled', count: numEnabled }, 
				{ type: 'Not enabled', count: numNotEnabled }
			];
			
			$scope.healthyGivenEnabledDataset = [
 				{ type: 'Healthy given enabled', count: numHealthyGivenEnabled }, 
				{ type: 'Unhealthy given enabled', count: numUnhealthyGivenEnabled }
			];
			
			$scope.nodeStatsStatus = 'loaded';
		}
		
		$http(request)
				.success(successHandler)
				.error(function() { $scope.nodeStatsStatus = 'error'; });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
