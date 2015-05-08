var nodeAlertsController = function() {
	var controller = function($scope, $http, paginationConfig, $routeParams) {
		$scope.model.nodeAlerts = {
			currentPage: 1,
			pageSelected: function() {
				$scope.nodeAlertsStatus = 'loading';
				var pageNumber = $scope.model.nodeAlerts.currentPage;
				var apiPageNumber = pageNumber - 1;
				var request = {
						method: 'GET',
						url: '/v2/nodes/search/find-node-alerts-by-service-instance?key=' + $routeParams.key + '&view=service-instance-nodes&page=' + apiPageNumber + '&size=' + paginationConfig.itemsPerPage + '&sort=name',
						headers: { 'Accept': 'application/hal+json' }
				}
				var successHandler = function(data) {
					$scope.nodeAlertsPage = data;
					$scope.metadata = $scope.nodeAlertsPage.metadata;
					$scope.nodeRows = nodePageToNodeRows($scope.nodeAlertsPage);
					$scope.nodeAlerts = $scope.nodeAlertsPage._embedded.items;
					$scope.nodeAlertsStatus = 'loaded';
				}
				$http(request)
						.success(successHandler)
						.error(function() { $scope.nodeAlertsStatus = 'error'; });
			}
		}
		$scope.model.nodeAlerts.pageSelected();
	}
	return [ '$scope', '$http', 'paginationConfig', '$routeParams', controller ];
}
