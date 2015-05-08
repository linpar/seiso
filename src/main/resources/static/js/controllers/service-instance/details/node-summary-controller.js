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
			$scope.nodeStatsStatus = 'loaded';
		}
		$http(request)
				.success(successHandler)
				.error(function() { $scope.nodeStatsStatus = 'error'; });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
