var environmentDetailsController = function() {
	var controller = function($scope, v2Api, $http, paginationConfig, $routeParams) {
		(function getEnvironment() {
			var successHandler = function(data) {
				var env = data;
				$scope.environment = env;
				$scope.model.page.title = pageTitle(env.name);
			}
			var errorHandler = function() { alert("Error while getting environment."); }
			v2Api.get('/v2/environments/' + $routeParams.key, successHandler, errorHandler);
		})();
		
		$scope.model.serviceInstances = {
			currentPage: 1,
			pageSelected: function() {
				
				(function getServiceInstances(pageNumber) {
					$scope.serviceInstanceListStatus = 'loading';
					var apiPageNumber = pageNumber - 1;
					var siRequest = {
							method: 'GET',
							url: '/v2/service-instances/search/find-by-environment?key=' + $routeParams.key
							    + '&page=' + apiPageNumber + '&size=' + paginationConfig.itemsPerPage + '&sort=key',
							headers: { 'Accept': 'application/hal+json' }
					}
					var siSuccessHandler = function(data) {
						var siPage = data;
						$scope.serviceInstances = siPage._embedded.items;
						$scope.serviceInstanceMetadata = siPage.metadata;
						$scope.serviceInstanceListStatus = 'loaded';
					}
					$http(siRequest)
							.success(siSuccessHandler)
							.error(function() { $scope.serviceInstanceListStatus = 'error'; });
				})($scope.model.serviceInstances.currentPage);
			}
		}
		
		$scope.model.serviceInstances.pageSelected();
	}
	
	return [ '$scope', 'v2Api', '$http', 'paginationConfig', '$routeParams', controller ];
}
