angular.module('seisoControllers', [])

		// Special
		.controller('HomeController', homeController())
		.controller('SearchController', searchController())
		.controller('MotdController', motdController())
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
