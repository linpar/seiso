var globalsController = function() {
	var controller = function($rootScope, $scope, $http, authService) {
		$scope.logout = authService.logout;
		
		authService.checkAuthentication(false);
		
		var getGlobalData = function() {
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
		getGlobalData();
	}
	return [ '$rootScope', '$scope', '$http', 'AuthService', controller ];
};
