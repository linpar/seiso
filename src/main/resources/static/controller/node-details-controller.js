angular.module('seisoControllers')
	.controller('NodeDetailsController', [ '$scope', '$http', '$routeParams',
			function($scope, $http, $routeParams) {
				$http.get('v1/nodes/' + $routeParams.name).success(function(data) {
					$scope.node = data;
					if ($scope.node != null) {
						$scope.serviceInstance = $scope.node.serviceInstance;
						if ($scope.serviceInstance != null) {
							$scope.service = $scope.serviceInstance.service;
							$scope.owner = $scope.service.owner;
							$scope.owner.fullName = $scope.owner.firstName + " " + $scope.owner.lastName;
							$scope.environment = $scope.serviceInstance.environment;
							$scope.dataCenter = $scope.serviceInstance.dataCenter;
							if ($scope.dataCenter != null) {
								$scope.region = $scope.dataCenter.region;
								if ($scope.region != null) {
									$scope.infrastructureProvider = $scope.region.infrastructureProvider;
								}
							}
							$scope.machine = $scope.node.machine;
						}
					}
				});
			} ]);
