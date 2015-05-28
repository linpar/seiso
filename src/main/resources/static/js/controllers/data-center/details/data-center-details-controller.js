var dataCenterDetailsController = function() {
	var controller = function($scope, v2Api, $http, paginationConfig, $routeParams) {
		(function getDataCenter() {
			var successHandler = function(data) {
				var dataCenter = data;
				$scope.dataCenter = dataCenter;
				$scope.serviceInstances = dataCenter._embedded.serviceInstances;
				$scope.loadBalancers = dataCenter._embedded.loadBalancers;
				$scope.model.page.title = pageTitle(dataCenter.name);
			}
			var errorHandler = function() { alert("Error while getting data center."); }
			v2Api.get('/v2/data-centers/' + $routeParams.key, successHandler, errorHandler);
		})();
		
		$scope.model.serviceInstances = {
			currentPage: 1,
			pageSelected: function() {
				(function getServiceInstances(pageNumber) {
					$scope.serviceInstanceListStatus = 'loading';
					var apiPageNumber = pageNumber - 1;
					var siRequest = {
							method: 'GET',
							url: '/v2/service-instances/search/find-by-data-center?key=' + $routeParams.key
							    + '&page=' + apiPageNumber + '&size=' + paginationConfig.itemsPerPage + '&sort=key',
							headers: { 'Accept': 'application/hal+json' }
					}
					var successHandler = function(data) {
						var page = data;
						$scope.serviceInstances = page._embedded.items;
						$scope.serviceInstanceMetadata = page.metadata;
						$scope.serviceInstanceListStatus = 'loaded';
					}
					$http(siRequest)
							.success(successHandler)
							.error(function() { $scope.serviceInstanceListStatus = 'error'; });
				})($scope.model.serviceInstances.currentPage);
			}
		}
		
		$scope.model.loadBalancers = {
			currentPage: 1,
			pageSelected: function() {
				(function getLoadBalancers(pageNumber) {
					$scope.loadBalancerListStatus = 'loading';
					var apiPageNumber = pageNumber - 1;
					var request = {
							method: 'GET',
							url: '/v2/load-balancers/search/find-by-data-center?key=' + $routeParams.key
							    + '&page=' + apiPageNumber + '&size=' + paginationConfig.itemsPerPage + '&sort=name',
							headers: { 'Accept': 'application/hal+json' }
					}
					var successHandler = function(data) {
						var page = data;
						$scope.loadBalancers = page._embedded.items;
						$scope.loadBalancerMetadata = page.metadata;
						$scope.loadBalancerListStatus = 'loaded';
					}
					$http(request)
							.success(successHandler)
							.error(function() { $scope.loadBalancerListStatus = 'error'; });
				})($scope.model.loadBalancers.currentPage);
			}
		}
		
		$scope.model.serviceInstances.pageSelected();
		$scope.model.loadBalancers.pageSelected();
	}
	return [ '$scope', 'v2Api', '$http', 'paginationConfig', '$routeParams', controller ];
}
