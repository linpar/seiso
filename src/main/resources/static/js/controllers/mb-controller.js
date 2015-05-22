var mbController = function() {
	var controller = function($scope) {
		$scope.model.page.title = "Myers-Briggs Personality Types";
	}
	return [ '$scope', controller ];
}

var mbProfileController = function() {
	var names = {
			"infp" : "The Healer",
			"infj" : "The Counselor",
			"intj" : "The Mastermind",
			"intp" : "The Architect",
			"isfj" : "The Protector",
			"isfp" : "The Composer",
			"istj" : "The Inspector",
			"istp" : "The Craftsman",
			"enfj" : "The Teacher",
			"enfp" : "The Champion",
			"entj" : "The Commander",
			"entp" : "The Visionary",
			"esfj" : "The Provider",
			"esfp" : "The Performer",
			"estj" : "The Supervisor",
			"estp" : "The Dynamo"
	}
	var controller = function($scope, $http, $routeParams) {
		var type = $routeParams.type;
		
		$scope.model.page.title = pageTitle(type.toUpperCase());
		$scope.type = type;
		$scope.pageHeader = type.toUpperCase() + " - " + names[type];
		
		var successHandler = function(data) {
			if (data) {
				$scope.same = data.same;
				$scope.allies = data.allies;
				$scope.enemies = data.enemies;
			}
		}
		var errorHandler = function() {
			alert("Couldn't get profile info");
		}
		
		var request = {
			method: 'GET',
			url: '/internal/mb/' + type,
			headers: { 'Accept': 'application/hal+json' }
		}
		$http(request)
				.success(successHandler)
				.error(errorHandler);
	}
	return [ '$scope', '$http', '$routeParams', controller ];
}
