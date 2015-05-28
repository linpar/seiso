var environmentListController = function() {
	var controller = function($scope, $http) {
		$scope.model.page.title = pageTitle('Environments');
		$http.get('/v1/environments')
				.success(function(data) { $scope.items = data; })
				.error(function() { alert('Error while getting environments.'); });
	}
	return [ '$scope', '$http', controller ];
};
