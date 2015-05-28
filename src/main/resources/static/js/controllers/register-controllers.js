angular.module('seisoControllers', [])

		// Special
		.controller('HomeController', homeController())
		.controller('SearchController', searchController())
		.controller('MBController', mbController())
		.controller('MBProfileController', mbProfileController())
		.controller('MotdController', motdController())
		.controller('NavController', navController())
		.controller('AdminController', adminController())
		
		// Items
		.controller('DataCenterListController', dataCenterListController())
		.controller('DataCenterDetailsController', dataCenterDetailsController())
		.controller('EnvironmentListController', environmentListController())
		.controller('EnvironmentDetailsController', environmentDetailsController())
		.controller('LoadBalancerListController', pagingController('Load Balancers', '/v1/load-balancers', 'name'))
		.controller('LoadBalancerDetailsController', loadBalancerDetailsController())
		.controller('MachineDetailsController', machineDetailsController())
		.controller('NodeAlertsController', nodeAlertsController())
		.controller('NodeBreakdownController', nodeBreakdownController())
		.controller('NodeSummaryController', nodeSummaryController())
		.controller('NodeDetailsController', nodeDetailsController())
		.controller('PersonListController', pagingController('People', '/v1/people', 'lastName,firstName'))
		.controller('PersonDetailsController', personDetailsController())
		
		.controller('ServiceListController', pagingController('Services', '/v1/services', 'name'))
		
		.controller('ServiceDetailsController', serviceDetailsController())
		.controller('ServiceServiceInstancesController', serviceServiceInstancesController())
		.controller('ServiceDocumentationController', serviceDocumentationController())
		
		.controller('ServiceInstanceListController', pagingController('Service Instances', '/v1/service-instances', 'key'))
		
		.controller('ServiceInstanceDetailsController', serviceInstanceDetailsController())
		.controller('ServiceInstanceNodesController', serviceInstanceNodesController())
		
		.controller('StatusListController', statusListController())
		.controller('TypeListController', typeListController())
		;
