angular.module('seiso', [ 'ngRoute', 'ngSanitize', 'ui.bootstrap', 'seisoFilters', 'seisoServices', 'seisoControllers' ])
	.config([ '$httpProvider', '$routeProvider', 'paginationConfig', function($httpProvider, $routeProvider, paginationConfig) {		
		$httpProvider.defaults.headers.common = {
			// TODO Migrate toward application/hal+json
			"Accept" : "application/json",
			// https://spring.io/blog/2015/01/12/the-login-page-angular-js-and-spring-security-part-ii 
			"X-Requested-With" : "XMLHttpRequest"
		};
		
		var route = function(controllerName, viewName) {
			return {
				controller: controllerName + 'Controller',
				templateUrl: 'view/' + viewName + '.html'
			};
		}
		
		var viewRoute = function(shortViewName) {
			return {
				templateUrl: "view/" + shortViewName + ".html"
			}
		}
		
		$routeProvider
				.when('/', route('Home', 'home/home'))
				.when('/search', route('Search', 'search/search'))
				.when('/login', route('Login', 'login/login'))
				.when('/admin', route('Admin', 'admin/index'))
				.when('/mb', viewRoute("mb/index"))
				.when('/mb/:type', viewRoute("mb/profile"))
				.when('/data-centers', viewRoute("items/data-center/list/data-center-list"))
				.when('/data-centers/:key', route('DataCenterDetails', 'items/data-center/details/data-center-details'))
				.when('/environments', viewRoute("items/environment/list/environment-list"))
				.when('/environments/:key', route('EnvironmentDetails', 'items/environment/details/environment-details')) 
				.when('/load-balancers', route('LoadBalancerList', 'items/load-balancer/list/load-balancer-list'))
				.when('/load-balancers/:name', route('LoadBalancerDetails', 'items/load-balancer/details/load-balancer-details'))
				.when('/machines/:name', route('MachineDetails', 'items/machine/details/machine-details'))
				.when('/nodes/:name', route('NodeDetails', 'items/node/details/node-details'))
				.when('/people', route('PersonList', 'items/person/list/person-list'))
				.when('/people/:username', route('PersonDetails', 'items/person/details/person-details'))
				.when('/services', route('ServiceList', 'items/service/list/service-list'))
				.when('/services/:key', viewRoute("items/service/details/service-details"))
				.when('/service-instances', route('ServiceInstanceList', 'items/service-instance/list/service-instance-list'))
				.when('/service-instances/:key', viewRoute("items/service-instance/details/service-instance-details"))
				.when('/statuses', route('StatusList', 'items/status/list/status-list'))
				.when('/types', route('TypeList', 'items/type/list/type-list'))
				.otherwise({ redirectTo : '/' });
				
		// Pagination configuration. Is this the right place to do this?
		paginationConfig.itemsPerPage = 50;
		paginationConfig.maxSize = 7;
		paginationConfig.boundaryLinks = true;
		// FIXME Want to use &laquo;, &lsaquo;, etc., but Angular is escaping the &. [WLW] 
		paginationConfig.firstText = '<<';
		paginationConfig.previousText = "<";
		paginationConfig.nextText = '>';
		paginationConfig.lastText = '>>';
	} ])
	
	// TODO The functions here belong in a service. See
	// http://stackoverflow.com/questions/11938380/global-variables-in-angularjs/11938785#11938785
	// https://docs.angularjs.org/misc/faq ("$rootScope exists, but it can be used for evil")
	.run([ '$rootScope', '$http', function($rootScope, $http) {
		$rootScope.model = {
			page: {
				title: 'Seiso'
			}
		};
		$rootScope.uri = function(repoKey, itemKey) {
			if (repoKey == null) {
				return '#/';
			} else if (itemKey == null) {
				return '#/' + repoKey;
			} else {
				return '#/' + repoKey + '/' + itemKey;
			}
		}
		$rootScope.displayName = function(person) {
			// TODO Somehow the firstNameLastName thing actually works even for service owner, but since I have no idea
			// how, I'm not going to depend upon this quite yet.
//			return person.displayName == null ? person.firstNameLastName : person.displayName;
			return person.displayName == null ? person.firstName + " " + person.lastName : person.displayName;
		}
	} ]);
