// TODO For errors, do something better than alert dialogs. :D


// =====================================================================================================================
// Special controllers
// =====================================================================================================================

var homeController = function() {
	var controller = function($scope, $http) {
		$scope.pageId = 'home';
		serviceGroupsMap = {};
		$http.get('v1/service-groups').success(function(data) {
			$scope.serviceGroups = data;
			$scope.serviceGroups.push({ "key" : "_ungrouped", "name" : "Ungrouped" });
			for (i = 0; i < data.length; i++) {
				serviceGroup = data[i];
				serviceGroup.services = [];
				serviceGroupsMap[serviceGroup.key] = serviceGroup;
			}
		});
		
		// FIXME If there are more than 300 services, we won't catch them all. We need a JS client for getting the
		// full list from the paging API. (The API will continue to page.) [WLW]
		$http.get('v1/services?page=0&size=300&sort=name').success(function(data) {
			services = data;
			for (i = 0; i < services.length; i++) {
				service = services[i];
				group = service.group;
				if (group === null) {
					serviceGroupsMap['_ungrouped'].services.push(service);
				} else {
					serviceGroupsMap[group.key].services.push(service);
				}
			}
		});
	}
	return [ '$scope', '$http', controller ];
}

var searchController = function() {
	var controller = function($rootScope, $scope, SearchService, $location) {
		$scope.searchService = SearchService;
		$scope.searchQuery = SearchService.getQuery();
		$scope.searchResults = SearchService.getResults();
		$scope.search = function() {
			SearchService.search(function() { 
				var searchResults = SearchService.getResults()['value'];
				$rootScope.searchResults = searchResults;
				$location.path('/search');
			});
		};
	}
	return [ '$rootScope', '$scope', 'SearchService', '$location', controller ];
}

// FIXME Break this up into multiple controllers so we don't have to load so much data at once. [WLW]
var adminController = function() {
	var controller = function($scope, $http) {
		console.log('Getting health');
		$http.get('health').success(function(data) {
			console.log('Got health');
			$scope.health = data;
			$scope.seisoLabel = (data.status === 'UP' ? 'success' : 'danger');
			$scope.diskLabel = (data.diskSpace.status === 'UP' ? 'success' : 'danger');
			$scope.dbLabel = (data.db.status === 'UP' ? 'success' : 'danger');
			$scope.rabbitLabel = (data.rabbit.status === 'UP' ? 'success' : 'danger');
		})
		
		console.log('Getting metrics');
		$http.get('metrics').success(function(data) {
			console.log('Got metrics');
			$scope.metrics = data;
		})
		
		console.log('Getting environment');
		$http.get('env').success(function(data) {
			console.log('Got environment');
			$scope.env = data;
			$scope.appConfig = data['applicationConfig: [classpath:/application.yml]'];
			$scope.systemConfig = data['systemProperties'];
		})
		
		console.log('Getting thread dump');
		$http.get('dump').success(function(data) {
			console.log('Got thread dump');
			$scope.threads = data;
		});
	}
	return [ '$scope', '$http', controller ];
}


// =====================================================================================================================
// Item controllers
// =====================================================================================================================

var pagingController = function(path, sortKey) {
	var controller = function($scope, paginationConfig, v1Api) {
		console.log("Creating new paging list controller");
		var pageSize = paginationConfig.itemsPerPage;
		
		var successHandler = function(data, status, headers) {
			var totalItems = headers('X-Pagination-TotalElements');
			var totalPages = headers('X-Pagination-TotalPages');
			
			// FIXME Handle no-items case [WLW]
			var lowerIndex = ($scope.model.currentPage - 1) * pageSize + 1;
			var upperIndex = Math.min(totalItems, $scope.model.currentPage * pageSize);
			
			$scope.totalItems = totalItems;
			$scope.totalPages = totalPages;
			$scope.lowerIndex = lowerIndex;
			$scope.upperIndex = upperIndex;
			$scope.items = data;
		};
		
		$scope.model.pageSelected = function() {
			var pageNumber = $scope.model.currentPage - 1;
			console.log("Page selected: path=" + path
					+ ", pageNumber=" + pageNumber
					+ ", pageSize=" + pageSize
					+ ", sortKey=" + sortKey);
			v1Api.getPage(path, pageNumber, pageSize, sortKey)
					.success(successHandler)
					.error(function() { alert('Error while getting page.'); });
		};
		
		// Initialize first page
		$scope.model.currentPage = 1;
		$scope.model.pageSelected();
	};
	
	return [ '$scope', 'paginationConfig', 'v1Api', controller ];
};

var dataCenterListController = function() {
	var controller = function($scope, $http, generalRegions) {
		var successHandler = function(data) {
			var srcProviders = data;
			var destProviders = {};
			for (i = 0; i < srcProviders.length; i++) {
			
				// Initialize provider data structure.
				var srcProvider = srcProviders[i];
				var providerKey = srcProvider.key;
				destProviders[providerKey] = {
					'name' : srcProvider.name,
					'specialRegions' : {}
				};
				var destProvider = destProviders[providerKey];
				for (j = 0; j < generalRegions.length; j++) {
					var generalRegion = generalRegions[j];
					destProvider.specialRegions[generalRegion.key] = [];
				};
				
				// Distribute the provider's special regions into into general regional buckets.
				destProvider = destProviders[providerKey];
				var srcSpecialRegions = srcProvider.regions;
				for (j = 0; j < srcSpecialRegions.length; j++) {
					var srcSpecialRegion = srcSpecialRegions[j];
					var generalRegionKey = srcSpecialRegion.regionKey;
					destProvider.specialRegions[generalRegionKey].push(srcSpecialRegion);
				}
			}
			
			$scope.generalRegions = generalRegions;
			$scope.infrastructureProviders = destProviders;
		}
		
		// Format providers for rendering. We want a matrix with general regions (NA, EU, APAC, SA) for columns and
		// providers for rows. Any given cell contains the provider's special regions (falling under the relevant
		// general region) and corresponding data centers.
		$http.get('/v1/infrastructure-providers')
				.success(successHandler)
				.error(function() { alert('Error while getting data centers.'); });
	}
	
	return [ '$scope', '$http', 'generalRegions', controller ];
}

var dataCenterDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		var successHandler = function(data) {
			$scope.dataCenter = data;
			// FIXME Move to separate search per https://github.com/ExpediaDotCom/seiso/issues/76
			$scope.serviceInstances = data.serviceInstances;
			$scope.loadBalancers = data.loadBalancers;
		}
		$http.get('/v1/data-centers/' + $routeParams.key)
				.success(successHandler)
				.error(function() { alert('Error while getting data center.'); });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var environmentListController = function() {
	var controller = function($scope, $http) {
		$http.get('/v1/environments')
				.success(function(data) { $scope.items = data; })
				.error(function() { alert('Error while getting environments.'); });
	}
	return [ '$scope', '$http', controller ];
};

var environmentDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		$http.get('/v1/environments/' + $routeParams.key)
				.success(function(data) {
					$scope.environment = data;
					// FIXME Move to separate search per https://github.com/ExpediaDotCom/seiso/issues/75
					$scope.serviceInstances = data.serviceInstances
				})
				.error(function() { alert('Error while getting environment.'); });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var loadBalancerDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		// TODO Move to service
		$http.get('/v1/load-balancers/' + $routeParams.name)
				.success(function(data) { $scope.loadBalancer = data; })
				.error(function() { alert('Error while getting load balancer.'); });
		$http.get('/v1/load-balancers/' + $routeParams.name + '?view=service-instances')
				.success(function(data) { $scope.serviceInstances = data.serviceInstances; })
				.error(function() { alert('Error while getting service instances.'); });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var machineDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		$http.get('/v1/machines/' + $routeParams.name)
				.success(function(data) {
					$scope.machine = data;
					$scope.nodes = data.nodes;
				})
				.error(function() { alert('Error while getting machine.'); });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var nodeDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		var successHandler = function(data) {
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
		}
		
		$http.get('/v1/nodes/' + $routeParams.name)
				.success(successHandler)
				.error(function() { alert('Error while getting node.'); });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var personDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		var successHandler = function(data) {
			$scope.person = data;
			$scope.person.firstNameLastName = data.firstName + ' ' + data.lastName;
		}
		$http.get('/v2/people/' + $routeParams.username, { headers: { 'Accept': 'application/hal+json' } })
				.success(successHandler)
				.error(function() { alert('Error while getting person.'); })
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var serviceDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		$http.get('/v1/services/' + $routeParams.key)
				.success(function(data) { $scope.service = data; })
				.error(function() { alert('Error while getting service.'); });
		$http.get('/v1/services/' + $routeParams.key + '?view=instances')
				.success(function(data) { $scope.serviceInstances = data.serviceInstances; })
				.error(function() { alert('Error while getting service instances.'); });
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var serviceInstanceDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		var request = {
			method: 'GET',
			url: '/v2/service-instances/' + $routeParams.key,
			headers: { 'Accept': 'application/hal+json' }
		}
		
		var successHandler = function(data) {
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
		}
		
		$http(request)
				.success(successHandler)
				.error(function() { alert('Error while getting service instance.'); });
	}
	
	return [ '$scope', '$http', '$routeParams', controller ];
}

var statusListController = function() {
	var controller = function($scope, $http) {
		// TODO Handle errors
		$http.get('/v1/status-types')
				.success(function(data) { $scope.statusTypes = data; });
		$http.get('/v1/health-statuses')
				.success(function(data) { $scope.healthStatuses = data; });
		$http.get('/v1/rotation-statuses')
				.success(function(data) { $scope.rotationStatuses = data; });
	}
	return [ '$scope', '$http', controller ];
};

// At some future point this will have other types too (e.g., dependency types, platform types, etc.). When that
// happens we can treat this like we're treating StatusListController above, with embedded GETs. [WLW]
var typeListController = function() {
	var controller = function($scope, $http) {
		$http.get('/v1/service-types')
				.success(function(data) { $scope.items = data; })
				.error(function() { alert('Error while getting service types.'); });
	};
	return [ '$scope', '$http', controller ];
};


// =====================================================================================================================
// Register controllers
// =====================================================================================================================

angular.module('seisoControllers', [])

		// Special
		.controller('HomeController', homeController())
		.controller('SearchController', searchController())
		.controller('AdminController', adminController())
		
		// Items
		.controller('DataCenterListController', dataCenterListController())
		.controller('DataCenterDetailsController', dataCenterDetailsController())
		.controller('EnvironmentListController', environmentListController())
		.controller('EnvironmentDetailsController', environmentDetailsController())
		.controller('LoadBalancerListController', pagingController('/v1/load-balancers', 'name'))
		.controller('LoadBalancerDetailsController', loadBalancerDetailsController())
		.controller('MachineDetailsController', machineDetailsController())
		.controller('NodeDetailsController', nodeDetailsController())
		.controller('PersonListController', pagingController('/v1/people', 'lastName,firstName'))
		.controller('PersonDetailsController', personDetailsController())
		.controller('ServiceListController', pagingController('/v1/services', 'name'))
		.controller('ServiceDetailsController', serviceDetailsController())
		.controller('ServiceInstanceListController', pagingController('/v1/service-instances', 'key'))
		.controller('ServiceInstanceDetailsController', serviceInstanceDetailsController())
		.controller('StatusListController', statusListController())
		.controller('TypeListController', typeListController())
		;
