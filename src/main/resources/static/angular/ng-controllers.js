// TODO For errors, do something better than alert dialogs. :D

var pageTitle = function(baseTitle) {
	return baseTitle + " - Seiso";
}

// =====================================================================================================================
// Special controllers
// =====================================================================================================================

var homeController = function() {
	var controller = function($scope, $http) {
		$scope.model.page.title = pageTitle('Home');
		$scope.serviceStatus = 'loading';
		
		var getServiceGroups = function(doneCallback) {
			$http.get('/v1/service-groups')
				.success(function(serviceGroups) { return doneCallback(null, serviceGroups); })
				.error(function(data) { return doneCallback(data, null); });
		}
		
		var getServices = function(doneCallback) {
			// FIXME If there are more than 300 services, we won't catch them all. We need a JS client for getting the
			// full list from the paging API. (The API will continue to page.) [WLW]
			$http.get('/v1/services?page=0&size=300&sort=name')
				.success(function(services) { return doneCallback(null, services); })
				.error(function(data) { return doneCallback(data, null); });
		}
		
		var iterator = function(f, doneCallback) { f(doneCallback); }
		async.map([getServiceGroups, getServices], iterator, function(err, results) {
			if (err) {
				$scope.serviceStatus = 'error';
				return;
			}
			
			var serviceGroups = results[0];
			var services = results[1];
			var serviceGroupsMap = {};
			
			serviceGroups.push({ "key" : "_ungrouped", "name" : "Ungrouped" });
			serviceGroups.forEach(function(group) {
				group.services = [];
				serviceGroupsMap[group.key] = group;
			});
			
			services.forEach(function(service) {
				var key = (service.group === null ? '_ungrouped' : service.group.key);
				serviceGroupsMap[key].services.push(service);
			});
			
			$scope.serviceGroups = serviceGroups;
			$scope.serviceStatus = 'loaded';
		});
	}
	
	return [ '$scope', '$http', controller ];
}

var searchController = function() {
	var controller = function($rootScope, $scope, SearchService, $location) {
		$scope.model.page.title = pageTitle('Search Results');
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
		$scope.model.page.title = pageTitle('Admin Console');
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

var pagingController = function(title, path, sortKey) {
	var controller = function($scope, paginationConfig, v1Api) {
		$scope.model.page.title = pageTitle(title);
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
		$scope.model.page.title = pageTitle('Data Centers');
		
		var successHandler = function(data) {
			var srcProviders = data;
			var destProviders = organizeDataCenters(srcProviders, generalRegions);
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
					var request = {
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
					$http(request)
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

var environmentListController = function() {
	var controller = function($scope, $http) {
		$scope.model.page.title = pageTitle('Environments');
		$http.get('/v1/environments')
				.success(function(data) { $scope.items = data; })
				.error(function() { alert('Error while getting environments.'); });
	}
	return [ '$scope', '$http', controller ];
};

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

var loadBalancerDetailsController = function() {
	var controller = function($scope, $http, $routeParams) {
		// TODO Move to service
		$http.get('/v1/load-balancers/' + $routeParams.name)
				.success(function(data) {
					$scope.model.page.title = pageTitle(data.name);
					$scope.loadBalancer = data;
				})
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
					$scope.model.page.title = pageTitle(data.name);
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
			$scope.model.page.title = pageTitle(data.name);
			$scope.node = data;
			if ($scope.node != null) {
				$scope.serviceInstance = $scope.node.serviceInstance;
				if ($scope.serviceInstance != null) {
					$scope.service = $scope.serviceInstance.service;
					$scope.owner = $scope.service.owner;
					if ($scope.owner != null) {
						$scope.owner.fullName = $scope.owner.firstName + " " + $scope.owner.lastName;
					}
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
			var fullName = data.firstName + ' ' + data.lastName;
			$scope.model.page.title = pageTitle(fullName);
			$scope.person = data;
			$scope.person.firstNameLastName = fullName;
		}
		$http.get('/v2/people/' + $routeParams.username, { headers: { 'Accept': 'application/hal+json' } })
				.success(successHandler)
				.error(function() { alert('Error while getting person.'); })
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}

var serviceDetailsController = function() {
	var controller = function($scope, v2Api, $http, $routeParams) {
		(function getService() {
			$http.get('/v1/services/' + $routeParams.key)
					.success(function(data) {
						$scope.model.page.title = pageTitle(data.name);
						$scope.service = data;
					})
					.error(function() { alert('Error while getting service.'); });
		})();
		
		(function getServiceInstances() {
			$http.get('/v1/services/' + $routeParams.key + '?view=instances')
					.success(function(data) { $scope.serviceInstances = data.serviceInstances; })
					.error(function() { alert('Error while getting service instances.'); });
		})();
		
		(function getServiceDocumentation() {
			$scope.serviceDocumentationStatus = 'loading';
			var successHandler = function(data) {
				$scope.docLinks = data;
				$scope.serviceDocumentationStatus = 'loaded';
			}
			var errorHandler = function() {
				$scope.serviceDocumentationStatus = 'error';
			}
			v2Api.get('/v2/services/' + $routeParams.key + '/doc-links', successHandler, errorHandler);
		})();
	}
	
	return [ '$scope', 'v2Api', '$http', '$routeParams', controller ];
}

// TODO Refactor as suggested above. (See serviceDocumentationController.)
var serviceInstanceDetailsController = function() {
	var controller = function($scope, v2Api, $http, paginationConfig, $routeParams) {
		(function getServiceInstance() {
			var successHandler = function(data) {
				var serviceInstance = data;
				var siEmbedded = serviceInstance._embedded;
				var service = siEmbedded.service;
				$scope.serviceInstance = serviceInstance;
				$scope.model.page.title = pageTitle(serviceInstance.key);
				$scope.dataCenter = siEmbedded.dataCenter;
				$scope.environment = siEmbedded.environment;
				$scope.ipAddressRoles = siEmbedded.ipAddressRoles;
				$scope.loadBalancer = siEmbedded.loadBalancer;
				$scope.ports = siEmbedded.ports;
				$scope.service = service;
				$scope.owner = service._embedded.owner;
				$scope.dashboards = siEmbedded.dashboards;
				$scope.checks = siEmbedded.seyrenChecks;
			}
			// TODO Do better error handling, like the examples below.
			var errorHandler = function() { alert("Error while getting service instance."); }
			v2Api.get('/v2/service-instances/' + $routeParams.key, successHandler, errorHandler);
		})();
		
		(function getNodeStats() {
			$scope.nodeStatsStatus = 'loading';
			var nodeStatsRequest = {
				method: 'GET',
				url: '/v2/service-instances/' + $routeParams.key + '/node-stats',
				headers: { 'Accept': 'application/hal+json' }
			}
			var nodeStatsSuccessHandler = function(data) {
				var nodeStats = data;
				enrichNodeStats(nodeStats);
				$scope.nodeStats = nodeStats;
				$scope.nodeStatsStatus = 'loaded';
			}
			$http(nodeStatsRequest)
					.success(nodeStatsSuccessHandler)
					.error(function() { $scope.nodeStatsStatus = 'error' });
		})();
		
		$scope.model.nodes = {
			currentPage: 1,
			pageSelected: function() {
				(function getNodes(pageNumber) {
					$scope.nodeListStatus = 'loading';
					var apiPageNumber = pageNumber - 1;
					var nodesRequest = {
						method: 'GET',
						url: '/v2/nodes/search/find-by-service-instance?key=' + $routeParams.key + '&view=service-instance-nodes&page=' + apiPageNumber + '&size=' + paginationConfig.itemsPerPage + '&sort=name',
						headers: { 'Accept': 'application/hal+json' }
					}
					var nodesSuccessHandler = function(data) {
						var nodePage = data;
						$scope.metadata = nodePage.metadata;
						$scope.nodeRows = nodePageToNodeRows(nodePage);
						$scope.nodeListStatus = 'loaded';
					}
					$http(nodesRequest)
							.success(nodesSuccessHandler)
							.error(function() { $scope.nodeListStatus = 'error'; });
				})($scope.model.nodes.currentPage);
			}
		}
		
		$scope.model.nodes.pageSelected();
	}
	
	return [ '$scope', 'v2Api', '$http', 'paginationConfig', '$routeParams', controller ];
}

var statusListController = function() {
	var controller = function($scope, $http) {
		$scope.model.page.title = pageTitle('Statuses');
		
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
		$scope.model.page.title = pageTitle('Types');
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
		.controller('LoadBalancerListController', pagingController('Load Balancers', '/v1/load-balancers', 'name'))
		.controller('LoadBalancerDetailsController', loadBalancerDetailsController())
		.controller('MachineDetailsController', machineDetailsController())
		.controller('NodeDetailsController', nodeDetailsController())
		.controller('PersonListController', pagingController('People', '/v1/people', 'lastName,firstName'))
		.controller('PersonDetailsController', personDetailsController())
		.controller('ServiceListController', pagingController('Services', '/v1/services', 'name'))
		.controller('ServiceDetailsController', serviceDetailsController())
		.controller('ServiceInstanceListController', pagingController('Service Instances', '/v1/service-instances', 'key'))
		.controller('ServiceInstanceDetailsController', serviceInstanceDetailsController())
		.controller('StatusListController', statusListController())
		.controller('TypeListController', typeListController())
		;
