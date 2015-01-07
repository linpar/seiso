angular.module('seisoControllers').controller('PersonDetailsController', [ '$scope', '$http', '$routeParams',
	function($scope, $http, $routeParams) {
		console.log("Getting person");
		$http.get('v2/people/' + $routeParams.username, { headers : { 'Accept' : 'application/hal+json' } })
				.success(function(data) {
					$scope.person = data;
					$scope.person.firstNameLastName = $scope.person.firstName + " " + $scope.person.lastName;
				});
	} ]);
