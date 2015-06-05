var globalsController = function() {
	var controller = function($rootScope, $http) {
		var request = {
			method: 'GET',
			url: '/internal/globals',
			headers: { 'Accept': 'application/hal+json' }
		}
		var successHandler = function(data) {
			$rootScope.globals = {
				nav: data.seisoNav,
				motd: data.motd,
				enableActions: data.enableActions
			}
		}
		var errorHandler = function() {
			$rootScope.globals = {
				globalsError: true
			}
		}
		$http(request)
				.success(successHandler)
				.error(errorHandler);
	}
	return [ '$rootScope', '$http', controller ];
};
