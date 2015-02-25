// TODO Move these into module?

var listController = function(path) {
	console.log("Initializing list controller for " + path);
	return [ '$scope', '$http', function($scope, $http) {
		console.log("Loading data from " + path);
		$http.get('v1/' + path).success(function(data) {
			$scope.items = data;
		});
	} ];
}

var pagingListController = function(path, sortKey) {
	return [ '$scope', '$http', '$routeParams', 'paginationConfig', function($scope, $http, $routeParams, paginationConfig) {
		$scope.pageSelected = function() {
			$scope.pageLoading = true;
			var apiPage = $scope.currentPage - 1;
			var pageSize = paginationConfig.itemsPerPage;
			$http.get('v1/' + path + '?page=' + apiPage + '&size=' + pageSize + '&sort=' + sortKey).success(function(data, status, headers) {
				var totalItems = headers('X-Pagination-TotalElements');
				var totalPages = headers('X-Pagination-TotalPages');
				
				// FIXME Handle no-items case [WLW]
				var lowerIndex = ($scope.currentPage - 1) * pageSize + 1;
				var upperIndex = Math.min(totalItems, $scope.currentPage * pageSize);
				
				// Disabling this, as I don't think there's a reason to hide it. That just creates a flicker.
//				$scope.showPageMeta = false;
				
				$scope.totalItems = totalItems;
				$scope.totalPages = totalPages;
				$scope.lowerIndex = lowerIndex;
				$scope.upperIndex = upperIndex;
				$scope.showPageMeta = true;
				$scope.items = data;
				$scope.pageLoading = false;
			});
		};
		
		// Initialize first page
		$scope.currentPage = 1;
		$scope.pageSelected();
	} ];
};

var seisoControllers = angular.module('seisoControllers', []);

// List controllers
seisoControllers
	.controller('EnvironmentListController', listController('environments'))
	.controller('LoadBalancerListController', pagingListController('load-balancers', 'name'))
	.controller('PersonListController', pagingListController('people', 'lastName,firstName'))
	.controller('ServiceListController', pagingListController('services', 'name'))
	.controller('ServiceInstanceListController', pagingListController('service-instances', 'key'))
	
	.controller('StatusListController', [ '$scope', '$http', function($scope, $http) {
			$http.get('v1/status-types').success(function(data) {
				$scope.statusTypes = data;
			});
			$http.get('v1/health-statuses').success(function(data) {
				$scope.healthStatuses = data;
			});
			$http.get('v1/rotation-statuses').success(function(data) {
				$scope.rotationStatuses = data;
			});
		} ])
		
	.controller('TypeListController', [ '$scope', '$http', function($scope, $http) {
			$http.get('v1/service-types').success(function(data) {
				$scope.serviceTypes = data;
			});
		} ])
	;

// Details controllers
seisoControllers
	.controller('DataCenterDetailsController', [ '$scope', '$http', '$routeParams',
			function($scope, $http, $routeParams) {
				$http.get('v1/data-centers/' + $routeParams.key).success(function(data) {
					$scope.dataCenter = data;
					$scope.serviceInstances = data.serviceInstances;
					$scope.loadBalancers = data.loadBalancers;
				});
			} ])
	.controller('EnvironmentDetailsController', [ '$scope', '$http', '$routeParams',
			function($scope, $http, $routeParams) {
				$scope.pageLoading = true;
				$http.get('v1/environments/' + $routeParams.key).success(function(data) {
					$scope.environment = data;
					// FIXME We need to move the service instances into a separate search, because some environments
					// have a lot. [WLW]
					$scope.serviceInstances = data.serviceInstances;
					$scope.pageLoading = false;
				});
			} ])
	.controller('LoadBalancerDetailsController', [ '$scope', '$http', '$routeParams',
			function($scope, $http, $routeParams) {
				$http.get('v1/load-balancers/' + $routeParams.name).success(function(data) {
					$scope.loadBalancer = data;
				});
				$http.get('v1/load-balancers/' + $routeParams.name + '?view=service-instances').success(function(data) {
					$scope.serviceInstances = data.serviceInstances;
				});
			} ])
	.controller('NodeSearchFormController', [ '$scope',
			function($scope) {
				$scope.submitForm = function() {
					console.log("Submitting " + JSON.stringify($scope.nodeSearch));
				}
				$scope.nodeSearch = { }
			} ])
	;
