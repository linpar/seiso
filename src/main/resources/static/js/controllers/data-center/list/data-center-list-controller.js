var dataCenterListController = function() {
	var controller = function($scope, $http, generalRegions) {
		$scope.model.page.title = pageTitle('Data Centers');
		
		var successHandler = function(data) {
			var srcProviders = data;
			var destProviders = organizeDataCenters(srcProviders, generalRegions);
			$scope.generalRegions = generalRegions;
			$scope.infrastructureProviders = destProviders;
		}
		
		// Format providers for rendering. We want a matrix with general regions (NA, EU, APAC, SA) for columns and
		// providers for rows. Any given cell contains the provider's special regions (falling under the relevant
		// general region) and corresponding data centers.
		$http.get('/v1/infrastructure-providers')
				.success(successHandler)
				.error(function() { alert('Error while getting data centers.'); });
	}
	
	return [ '$scope', '$http', 'generalRegions', controller ];
}
