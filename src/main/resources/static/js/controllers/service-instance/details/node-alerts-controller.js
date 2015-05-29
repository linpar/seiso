var nodeAlertsController = function() {
	var controller = function($scope, v2Api, paginationConfig, $routeParams) {
		$scope.model.nodeAlerts = {
			currentPage: 1,
			pageSelected: function() {
				$scope.nodeAlertsStatus = 'loading';
				var pageNumber = $scope.model.nodeAlerts.currentPage;
				var apiPageNumber = pageNumber - 1;
				var path = '/v2/nodes/search/find-node-alerts-by-service-instance?key='
					+ $routeParams.key + '&view=service-instance-nodes&page=' + apiPageNumber
					+ '&size=' + paginationConfig.itemsPerPage + '&sort=name';
				var successHandler = function(data) {
					$scope.nodeAlertsPage = data;
					$scope.metadata = $scope.nodeAlertsPage.metadata;
					$scope.nodeRows = nodePageToNodeRows($scope.nodeAlertsPage);
					$scope.nodeAlerts = $scope.nodeAlertsPage._embedded.items;
					$scope.nodeAlertsStatus = 'loaded';
				}
				var errorHandler = function() { $scope.nodeAlertsStatus = 'error'; }
				v2Api.get(path, successHandler, errorHandler);
			}
		}
		$scope.model.nodeAlerts.pageSelected();
	}
	return [ '$scope', 'v2Api', 'paginationConfig', '$routeParams', controller ];
}
