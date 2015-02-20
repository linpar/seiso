angular.module('seisoFilters', [])
	.filter('breakPaths', function() {
		return function(path) {
			if (typeof path === 'undefined') {
				return null;
			} else {
				// &#8203; is a zero-width space.
				return (path === null ? null : path.replace(/\:/g, "&#8203;:").replace(/\//g, "&#8203;/"));
			}
		}
	})
	.filter('breakPackages', function() {
		return function(path) {
			if (typeof path === 'undefined') {
				return null;
			} else {
				// &#8203; is a zero-width space.
				return (path === null ? null : path.replace(/\./g, "&#8203;.").replace(/\$/g, "&#8203;$"));
			}
		}
	});

angular.module('seiso', [ 'ngRoute', 'ngSanitize', 'ui.bootstrap', 'seisoControllers', 'seisoFilters' ])
	.constant('generalRegions', [
		{ 'key' : 'na', 'name' : 'North America' },
		{ 'key' : 'eu', 'name' : 'Europe' },
		{ 'key' : 'apac', 'name' : 'Asia/Pacific' },
		{ 'key' : 'sa', 'name' : 'South America' }
	])
	.config([ '$httpProvider', '$routeProvider', function($httpProvider, $routeProvider) {
		$httpProvider.defaults.headers.common = {
			// TODO Migrate toward application/hal+json
			'Accept' : 'application/json',
			'X-User-Agent' : 'Seiso UI'
		};
		
		$routeProvider.when('/', {
			controller : 'HomeController',
			templateUrl : 'view/home/home.html'
		}).when('/admin', {
			controller : 'AdminController',
			templateUrl : 'view/admin/index.html'
		}).when('/docs/user', {
			templateUrl : 'view/docs/user/index.html'
		}).when('/docs/api-v1-tutorial', {
			templateUrl : 'view/docs/api-v1-tutorial/index.html'
		}).when('/docs/api-v1-reference', {
			templateUrl : 'view/docs/api-v1-reference/index.html'
		}).when('/docs/ops', {
			templateUrl : 'view/docs/ops/index.html'
		}).when('/data-centers', {
			controller : 'DataCenterListController',
			templateUrl : 'view/items/data-center/list/data-center-list.html'
		}).when('/data-centers/:key', {
			controller : 'DataCenterDetailsController',
			templateUrl : 'view/items/data-center/details/data-center-details.html'
		}).when('/environments', {
			controller : 'EnvironmentListController',
			templateUrl : 'view/items/environment/list/environment-list.html'
		}).when('/environments/:key', {
			controller : 'EnvironmentDetailsController',
			templateUrl : 'view/items/environment/details/environment-details.html'
		}).when('/load-balancers', {
			controller : 'LoadBalancerListController',
			templateUrl : 'view/items/load-balancer/list/load-balancer-list.html'
		}).when('/load-balancers/:name', {
			controller : 'LoadBalancerDetailsController',
			templateUrl : 'view/items/load-balancer/details/load-balancer-details.html'
		}).when('/machines', {
			templateUrl : 'view/items/machine/search/machine-search.html'
		}).when('/machines/:name', {
			controller : 'MachineDetailsController',
			templateUrl : 'view/items/machine/details/machine-details.html'
		}).when('/nodes', {
			controller : 'NodeSearchController',
			templateUrl : 'view/items/node/search/node-search.html'
		}).when('/nodes/:name', {
			controller : 'NodeDetailsController',
			templateUrl : 'view/items/node/details/node-details.html'
		}).when('/people', {
			controller : 'PersonListController',
			templateUrl : 'view/items/person/list/person-list.html'
		}).when('/people/:username', {
			controller : 'PersonDetailsController',
			templateUrl : 'view/items/person/details/person-details.html'
		}).when('/services', {
			controller : 'ServiceListController',
			templateUrl : 'view/items/service/list/service-list.html'
		}).when('/services/:key', {
			controller : 'ServiceDetailsController',
			templateUrl : 'view/items/service/details/service-details.html'
		}).when('/service-instances', {
			controller : 'ServiceInstanceListController',
			templateUrl : 'view/items/service-instance/list/service-instance-list.html'
		}).when('/service-instances/:key', {
			controller : 'ServiceInstanceDetailsController',
			templateUrl : 'view/items/service-instance/details/service-instance-details.html'
		}).when('/statuses', {
			controller : 'StatusListController',
			templateUrl : 'view/items/status/list/status-list.html'
		}).when('/types', {
			controller : 'TypeListController',
			templateUrl : 'view/items/type/list/type-list.html'
		}).when('/search', {
			controller : 'SearchController',
			templateUrl : 'view/search/search.html'
		}).otherwise({
			redirectTo : '/'
		});
	} ])
	
	// http://stackoverflow.com/questions/21362712/html-file-as-content-in-bootstrap-popover-in-angularjs-directive
	.directive('rotationDetailsPopover', function($compile, $templateCache, $http) {
		return {
			restrict : "A",
			link : function($scope, $element, $attrs) {
				
				// Get the template here, not outside this function. Otherwise the behavior is all funky when the user
				// activates multiple popups, applies the filter, etc.
				// http://stackoverflow.com/questions/16122139/angular-js-jquery-html-string-parsing-in-1-9-1-vs-1-8-3
				var template = $templateCache.get("rotationDetailsPopover.html");
				template = angular.element($.trim(template));
				
				$http.get($attrs.rotationDetailsPopover).success(function(data) {
					$scope.ipAddress = data;
				});
				
				var popoverContent = $compile(template)($scope);
				$($element).popover({
					title : "Rotation Details",
					content : popoverContent,
					placement : "top",
					html : true,
					date : $scope.date
				});
			}
		};
	})
	
	// binds 'enter' key press to a provided function
	.directive('ngEnter', function () {
		return function (scope, element, attrs) {
			element.bind("keydown keypress", function (event) {
				if(event.which === 13) {
					scope.$apply(function () {
						scope.$eval(attrs.ngEnter);
					});
					event.preventDefault();
				}
			});
		};
	})	

	// paginationConfig is an existing constant. We're updating it here.
	.run(function($rootScope, paginationConfig, generalRegions) {
		$rootScope.uri = function(repoKey, itemKey) {
			if (repoKey == null) {
				return '#/';
			} else if (itemKey == null) {
				return '#/' + repoKey;
			} else {
				return '#/' + repoKey + '/' + itemKey;
			}
		}
		$rootScope.docsUri = function(page) {
			return '#/docs/' + page;
		}
		
		paginationConfig.itemsPerPage = 100;
		paginationConfig.maxSize = 7;
		paginationConfig.boundaryLinks = true;
		// FIXME Want to use &laquo;, &lsaquo;, etc., but Angular is escaping the &. [WLW] 
		paginationConfig.firstText = '<<';
		paginationConfig.previousText = '<';
		paginationConfig.nextText = '>';
		paginationConfig.lastText = '>>';
		
		$rootScope.generalRegions = generalRegions;
	});
