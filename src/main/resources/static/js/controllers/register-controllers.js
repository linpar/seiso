angular.module('seisoControllers', [])

		// Special
		.controller('HomeController', homeController())
		.controller('GlobalsController', globalsController())
		.controller('LoginController', loginController())
		.controller('SearchController', searchController())
		.controller('MBController', mbController())
		.controller('MBProfileController', mbProfileController())
		.controller('AdminController', adminController())
		
		.controller('EosConvictController', eosConvictController())
		.controller('EosDeployController', eosDeployController())
		.controller('EosInterrogateController', eosInterrogateController())
		.controller('EosMaintenanceModeController', eosMaintenanceModeController())
		.controller('EosReloadController', eosReloadController())
		.controller('EosSetActiveController', eosSetActiveController())
		.controller('EosSoakController', eosSoakController())
		
		// Items
		.controller('DataCenterListController', dataCenterListController())
		.controller('DataCenterDetailsController', dataCenterDetailsController())
		.controller('EnvironmentListController', listController('Environments', '/v2/environments'))
		.controller('EnvironmentDetailsController', environmentDetailsController())
		.controller('LoadBalancerListController', pagingController('Load Balancers', '/v1/load-balancers', 'name'))
		.controller('LoadBalancerDetailsController', loadBalancerDetailsController())
		.controller('MachineDetailsController', machineDetailsController())
		.controller('NodeAlertsController', nodeAlertsController())
		.controller('NodeBreakdownController', nodeBreakdownController())
		.controller('NodeSummaryController', nodeSummaryController())
		.controller('NodeDetailsController', nodeDetailsController())
		
		// TODO Consider using the display name here, but if we do, we'll need to parse the displayName in AD. [WLW]
		.controller('PersonListController', pagingController('People', '/v1/people', 'lastName,firstName'))
		
		.controller('PersonDetailsController', personDetailsController())
		.controller('ServiceListController', pagingController('Services', '/v1/services', 'name'))
		.controller('ServiceDetailsController', serviceDetailsController())
		.controller('ServiceServiceInstancesController', serviceServiceInstancesController())
		.controller('ServiceDocumentationController', serviceDocumentationController())
		.controller('ServiceInstanceListController', pagingController('Service Instances', '/v1/service-instances', 'key'))
		.controller('ServiceInstanceDetailsController', serviceInstanceDetailsController())
		.controller('ServiceInstanceNodesController', serviceInstanceNodesController())
		.controller('ServiceInstanceDependenciesController', serviceInstanceDependenciesController())
		.controller('ServiceInstanceDependentsController', serviceInstanceDependentsController())
		.controller('StatusListController', statusListController())
		.controller('TypeListController', listController('Types', '/v2/service-types'))
		;
