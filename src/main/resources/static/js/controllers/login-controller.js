var loginController = function() {
	var controller = function($scope, authService) {
		$scope.credentials = {};
		$scope.login = authService.login;
	}
	return [ '$scope', 'AuthService', controller ];
}
