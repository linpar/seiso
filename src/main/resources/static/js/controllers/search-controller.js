var searchController = function() {
	var controller = function($rootScope, $scope, SearchService, $location) {
		$scope.model.page.title = pageTitle('Search Results');
		$scope.searchService = SearchService;
		$scope.searchQuery = SearchService.getQuery();
		$scope.searchResults = SearchService.getResults();
		$scope.search = function() {
			SearchService.search(function() { 
				var searchResults = SearchService.getResults()['value'];
				$rootScope.searchResults = searchResults;
				$location.path('/search');
			});
		};
	}
	return [ '$rootScope', '$scope', 'SearchService', '$location', controller ];
}
